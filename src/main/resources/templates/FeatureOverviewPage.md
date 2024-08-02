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

{{#tests}}
**Description:** {{description}}


**Given**
    {{#givenLines}}
    {{.}}
    {{/givenLines}}

**When**
    {{#whenLines}}
    {{.}}
    {{/whenLines}}

**When**
    {{#thenLines}}
    {{.}}
    {{/thenLines}}

{{/tests}}

{{/feature}}
