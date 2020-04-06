package org.centralperf.sampler.driver.jmeter.helper;

import com.github.dockerjava.api.DockerClient;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Various utilities to interact with Docker containers and images
 *
 * @since 1.3
 */
public class DockerHelper {

    private static final Logger log = LoggerFactory.getLogger(DockerHelper.class);

    /**
     * Copy a file to the container
     *
     * @param dockerClient   Docker client
     * @param containerId    ID of target container
     * @param inputFile      File to copy
     * @param targetFileName Name of the file in the container
     * @param targetPath     Path where the file will be copied
     * @throws IOException If unable to copy
     */
    public static void copyFileToContainer(DockerClient dockerClient, String containerId, File inputFile, String targetFileName, String targetPath) throws IOException {
        Path tmp = Files.createTempFile("", ".tar");
        TarArchiveOutputStream tarArchiveOutputStream = new TarArchiveOutputStream(new BufferedOutputStream(Files.newOutputStream(tmp)));
        tarArchiveOutputStream.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        TarArchiveEntry tarArchiveEntry = (TarArchiveEntry) tarArchiveOutputStream.createArchiveEntry(inputFile, targetFileName);
        tarArchiveEntry.setSize(inputFile.length());
        tarArchiveOutputStream.putArchiveEntry(tarArchiveEntry);
        try (InputStream in = new FileInputStream(inputFile)) {
            IOUtils.copy(in, tarArchiveOutputStream, (int) inputFile.length());
        }
        tarArchiveOutputStream.flush();
        tarArchiveOutputStream.closeArchiveEntry();
        tarArchiveOutputStream.finish();
        tarArchiveOutputStream.close();
        dockerClient
                .copyArchiveToContainerCmd(containerId)
                .withTarInputStream(new BufferedInputStream(new FileInputStream(tmp.toFile())))
                .withRemotePath(targetPath)
                .exec();
    }

    /**
     * Get text file content from container
     *
     * @param dockerClient      Docker client
     * @param containerId       Id of target container
     * @param containerFilepath Path of the file in the container
     * @return the content of the file in the container
     * @throws IOException If unable to access or retrieve the file
     */
    public static String getFileContentFromContainer(DockerClient dockerClient, String containerId, String containerFilepath) throws IOException {
        InputStream response = dockerClient.copyArchiveFromContainerCmd(containerId, containerFilepath)
                .exec();
        try (TarArchiveInputStream tarInputStream = new TarArchiveInputStream(response)) {
            tarInputStream.getNextTarEntry();
            return consumeAsString(tarInputStream);
        }

    }

    private static String consumeAsString(InputStream response) throws IOException {
        try (StringWriter logwriter = new StringWriter()) {
            LineIterator itr = IOUtils.lineIterator(response, "UTF-8");
            while (itr.hasNext()) {
                String line = itr.next();
                logwriter.write(line + (itr.hasNext() ? "\n" : ""));
            }
            return logwriter.toString();
        }
    }
}
