{{#feature}}
# {{{name}}}

{{{description}}}

## Epics

{{{#epics}}}
- [{{{id}}}]({{{href}}})
{{{/epics}}}

## Stories

{{{#stories}}}
- [{{{id}}}]({{{href}}})
{{{/stories}}}

## Behavior

{{#scenarios}}

### {{index}}. {{{description}}}

{{#tests}}

**Given**

{{#givenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;{{{.}}}

{{/givenLines}}

**When**

{{#whenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;{{{.}}}

{{/whenLines}}

**Then**

{{#thenLines}}
&nbsp;&nbsp;&nbsp;&nbsp;{{{.}}}

{{/thenLines}}

{{/tests}}
{{/scenarios}}

{{/feature}}
