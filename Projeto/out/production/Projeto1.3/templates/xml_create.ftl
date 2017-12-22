<?xml version="1.0" encoding="utf-8" ?>
<!DOCTYPE model SYSTEM "../metamodels/model.dtd">
<model name="${name}">
<#list classes as class>
    <class name="${class.name}">
        <#list class.attributes as attribute>
        <attribute name="${attribute.name}" type="${attribute.type}" />
        </#list>
        <#list class.relations as rl>
        <relation name="${rl.name}" type="${rl.type}" />
        </#list>
    </class>
</#list>
</model>
