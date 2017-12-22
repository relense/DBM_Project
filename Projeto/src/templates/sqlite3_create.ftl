<#assign int_types = ['int']>
<#assign real_types = ['double', 'float']>
<#assign text_types = ['String', 'Date']>

<#list classes as class>
/* Create table ${class.name} */
CREATE TABLE ${class.name} (
<#if class.attributes?has_content>
    ${class.name?lower_case}Id INTEGER PRIMARY KEY AUTOINCREMENT,
<#else>
        ${class.name?lower_case}Id INTEGER PRIMARY KEY AUTOINCREMENT
</#if>
<#list class.attributes as attr>
    <#if int_types?seq_contains(attr.type)>
    ${attr.name?lower_case} INTEGER<#sep>,</#sep>
    <#elseif real_types?seq_contains(attr.type)>
    ${attr.name?lower_case} REAL<#sep>,</#sep>
    <#elseif text_types?seq_contains(attr.type)>
    ${attr.name?lower_case} TEXT<#sep>,</#sep>
    </#if>
</#list>
);
</#list>


<#if relation?has_content>
<#list relation as rl, rlk>
CREATE TABLE ${rl.name}${rlk.name}(
    FK_${rl.name?lower_case}_ID INTEGER<#sep>,</#sep>
    FK_${rlk.name?lower_case}_ID INTEGER<#sep>,</#sep>
);
</#list>
</#if>

<#list classes as class>
<#if class.foreignKeys?has_content>
<#list class.foreignKeys as fk>
    <#assign name = fk?lower_case + "Id">
ALTER TABLE ${class.name} ADD COLUMN FK_${name} INTEGER REFERENCES ${fk?capitalize}(${fk?lower_case}Id);
</#list>
</#if>
</#list>

