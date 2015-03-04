<#include "header.ftl">

	<#include "menu.ftl">
	
	<div class="page-header">
		<h1><#escape x as x?xml>${content.title}</#escape></h1>
	</div>

    <#if (content.date)??><p><em>${content.date?string("dd MMMM yyyy")}</em></p></#if>

	<p>${content.body}</p>

	<hr />

<#include "footer.ftl">