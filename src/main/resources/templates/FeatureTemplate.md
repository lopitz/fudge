{{#feature}}
# {{name}}

{{description}}

## Epics
not available yet, as JGiven doesn't allow to generate custom elements in the JSON report.
{{#epics}}
- [id](href)
{{/epics}}
 
## Stories
not available yet, as JGiven doesn't allow to generate custom elements in the JSON report.
{{#stories}}
- [id](href)
{{/stories}}

## Behavior

{{#scenarios}}
**Description:** {{description}}

{{#tests}}
**Given**
&nbsp;&nbsp;&nbsp;&nbsp;{{#givenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;{{.}}
&nbsp;&nbsp;&nbsp;&nbsp;{{/givenLines}}

**When**
&nbsp;&nbsp;&nbsp;&nbsp;{{#whenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;{{.}}
&nbsp;&nbsp;&nbsp;&nbsp;{{/whenLines}}

**When**
&nbsp;&nbsp;&nbsp;&nbsp;{{#thenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;{{.}}
&nbsp;&nbsp;&nbsp;&nbsp;{{/thenLines}}

{{/tests}}
{{/scenarios}}

{{/feature}}
