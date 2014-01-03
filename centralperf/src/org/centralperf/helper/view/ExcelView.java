package org.centralperf.helper.view;

import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.util.AreaReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Name;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.centralperf.model.Run;
import org.centralperf.model.Sample;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
 
/**
 * This class builds an Excel spreadsheet document using Apache POI library.
 * @author Charles Le Gallic
 */
@Component
public class ExcelView extends AbstractPOIExcelView {
	
	// Name of the sheet with data in the Excel Workbook
	private static final String DATA_SHEET_NAME = "cp_data";
	
	private static final String PROJECT_NAME_CELL_NAME = "projectName";
	private static final String RUN_LABEL_CELL_NAME = "runLabel";
	private static final String RUN_DESCRIPTION_CELL_NAME = "runDescription";
	private static final String START_DATE_CELL_NAME = "startDate";
	private static final String GENERATED_ON_CELL_NAME = "generatedOn";
 
	private static final Logger log = LoggerFactory.getLogger(ExcelView.class);
	
    @Override
    protected void buildExcelDocument(Map<String, Object> model,
            Workbook workbook, HttpServletRequest request, HttpServletResponse response)
            throws Exception {
    	
    	log.debug("Generating Excel report from run samples");
    	
    	 // Set the headers
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Disposition", "attachment; filename=central_perf_result.xlsx");
    	
        // get data model which is passed by the Spring container
        Run run = (Run) model.get("run");
        
        // Set run summary informations
        setCellValueByName(PROJECT_NAME_CELL_NAME, run.getProject().getName(), workbook);
        setCellValueByName(RUN_LABEL_CELL_NAME, run.getLabel(), workbook);
        setCellValueByName(RUN_DESCRIPTION_CELL_NAME, run.getComment(), workbook);
        setCellValueByName(START_DATE_CELL_NAME, run.getStartDate().toString(), workbook);
        setCellValueByName(START_DATE_CELL_NAME, run.getStartDate().toString(), workbook);
        setCellValueByName(GENERATED_ON_CELL_NAME, "" + unixTimestamp2ExcelTimestampconvert(new Date().getTime()), workbook);
        
        // Populate data sheet
        XSSFSheet dataSheet = (XSSFSheet) workbook.getSheet(DATA_SHEET_NAME);
        // Set date style for first column
        CellStyle dateStyle = workbook.createCellStyle();
        CreationHelper createHelper = workbook.getCreationHelper();
        dateStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy/mm/dd"));        
        dataSheet.setDefaultColumnStyle(0, dateStyle); 
        
        // Add samples
        for(int i = 0; i < run.getSamples().size(); i++){
        	Sample sample = run.getSamples().get(i);
        	XSSFRow dataRow = dataSheet.createRow(i + 1);
        	if(sample.getTimestamp() !=null){
	        	dataRow.createCell(0).setCellValue(unixTimestamp2ExcelTimestampconvert(sample.getTimestamp().getTime()));
	        	dataRow.createCell(1).setCellValue(sample.getElapsed());
	        	dataRow.createCell(2).setCellValue(sample.getSampleName());
	        	dataRow.createCell(3).setCellValue(sample.getStatus());
	        	dataRow.createCell(4).setCellValue(sample.getReturnCode());
	        	dataRow.createCell(5).setCellValue(sample.getSizeInOctet());
	        	dataRow.createCell(6).setCellValue(sample.getGrpThreads());
	        	dataRow.createCell(7).setCellValue(sample.getAllThreads());
	        	dataRow.createCell(8).setCellValue(sample.getLatency());
        	}
        }

        
        // Return generated sheet
        OutputStream outStream = null;
        try {
            outStream = response.getOutputStream();
            workbook.write(outStream);
            outStream.flush();
        } finally {
            outStream.close();
        }               
         
    }
    
    private double unixTimestamp2ExcelTimestampconvert(long unixTimestamp){
    	return (unixTimestamp / 86400000D) + 25569D;
    }
    
    /**
     * Set the value of a cell by locating it by it's name
     * @param cellName
     * @param cellValue
     * @param workbook
     */
    private void setCellValueByName(String cellName, String cellValue, Workbook workbook){
    	Cell cell = getCellByName(cellName, workbook);
    	if(cell != null){
    		cell.setCellValue(cellValue);
    	}
    }
    
    /**
     * Retrieve a cell in workbook by its name
     * @param cellName
     * @param workbook
     * @return the cell found, null if multiple cells or not found
     */
    private Cell getCellByName(String cellName, Workbook workbook){
        int namedCellIdx = workbook.getNameIndex(cellName);
        Name aNamedCell = workbook.getNameAt(namedCellIdx);
        
        // retrieve the cell at the named range and test its contents
        AreaReference aref = new AreaReference(aNamedCell.getRefersToFormula());
        if(aref.isSingleCell()){
            CellReference cref = aref.getFirstCell();
            Sheet s = workbook.getSheet(cref.getSheetName());
            Row r = s.getRow(cref.getRow());
            Cell c = r.getCell(cref.getCol());
            return c;
        }
        return null;
    }

	@Override
	protected Workbook createWorkbook() {
		return new XSSFWorkbook();
	}
}
