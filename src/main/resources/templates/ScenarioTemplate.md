# {{{featureName}}}

{{#scenario}}
## {{{index}}}. {{{description}}}

connected epics: {{#epics}}[{{{id}}}]({{{href}}}) {{/epics}}

connected stories: {{#stories}}[{{{id}}}]({{{href}}}) {{/stories}}

{{#tests}}

**Given**

{{#givenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;
{{#value}}
{{#wordGroup}}{{{.}}}{{/wordGroup}}{{#parameter}}**&lt;{{{.}}}&gt;**{{/parameter}}
{{/value}}
{{#table}}

| {{#header}} {{{.}}} | {{/header}}
|{{#header}}-|{{/header}}
{{#rows}}
| {{#columns}} {{{.}}} | {{/columns}}
{{/rows}}
{{/table}}

{{/givenLines}}

**When**

{{#whenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;
{{#value}}
{{#wordGroup}}{{{.}}}{{/wordGroup}}{{#parameter}}**&lt;{{{.}}}&gt;**{{/parameter}}
{{/value}}
{{#table}}

| {{#header}} {{{.}}} | {{/header}}
|{{#header}}-|{{/header}}
{{#rows}}
| {{#columns}} {{{.}}} | {{/columns}}
{{/rows}}
{{/table}}

{{/whenLines}}

**Then**

{{#thenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;
{{#value}}
{{#wordGroup}}{{{.}}}{{/wordGroup}}{{#parameter}}**&lt;{{{.}}}&gt;**{{/parameter}}
{{/value}}
{{#table}}

| {{#header}} {{{.}}} | {{/header}}
|{{#header}}-|{{/header}}
{{#rows}}
| {{#columns}} {{{.}}} | {{/columns}}
{{/rows}}
{{/table}}

{{/thenLines}}

{{#cases}}
| case number | {{#parameterNames}} {{{.}}} |{{/parameterNames}}
|-|{{#parameterNames}}-|{{/parameterNames}}
{{#cases}}
| {{number}} | {{#parameters}} {{{value}}} |{{/parameters}}
{{/cases}}
{{/cases}}

{{/tests}}
{{/scenario}}
