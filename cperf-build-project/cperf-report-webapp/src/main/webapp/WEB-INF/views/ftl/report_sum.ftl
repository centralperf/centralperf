<#import 'macros/layout.macro.ftl' as layout>
<#import "spring.ftl" as spring />

<@layout.main title="Report">

<script type='text/javascript'>
	<#include "graphs/sum.ftl">
</script>
<div id='sumChart'></div>


</@layout.main>