<#include "header.ftl">

    <#include "menu.ftl">

    <div class="page-header">
        <h1>Innovation Through Cooperation</h1>
    </div>

    <#list docs as doc>
        <#if (doc.status == "published")>
            <p><a href="${doc.uri}"><#escape x as x?xml>${doc.title}</#escape></a></p>
            <#if (doc.description)??>
                <p style="font-size:80%;text-indent: 50px;">${doc.description}</p>
            </#if>
        </#if>
    </#list>

    <hr />

<#include "footer.ftl">