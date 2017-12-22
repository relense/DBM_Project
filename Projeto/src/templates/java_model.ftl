<#assign relation = relation>

<#list classes as class>

<#assign name = class.name />
<#assign pkg = class.pkg />
<#assign foreignKeys = class.foreignKeys />
<#assign attributes = class.attributes />
<#assign required = class.required />
<#include "java_class.ftl" />

</#list>