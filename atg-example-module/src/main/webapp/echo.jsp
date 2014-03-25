<%@ taglib prefix="dsp" uri="dsp" %>
<dsp:page>
    <dsp:droplet name="/atg/tools/example/Echo">
        <dsp:param name="input" value="Hello, world!"/>
        <dsp:oparam name="output">
            <dsp:getvalueof param="data"/>
        </dsp:oparam>
    </dsp:droplet>
</dsp:page>