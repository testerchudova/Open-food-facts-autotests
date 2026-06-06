<#ftl output_format="HTML">
<#if data??>
<h3>Request</h3>
<div><b><#if data.method??>${data.method}<#else>GET</#if></b> <#if data.url??>${data.url}<#else>Unknown URL</#if></div>

<#if data.body??>
<h4>Body</h4>
<pre class="preformated-text">${data.body}</pre>
</#if>

<#if (data.headers)?has_content>
<h4>Headers</h4>
<#list data.headers as name, value>
<div>${name}: ${value!"null"}</div>
</#list>
</#if>

<#if (data.cookies)?has_content>
<h4>Cookies</h4>
<#list data.cookies as name, value>
<div>${name}: ${value!"null"}</div>
</#list>
</#if>
</#if>
