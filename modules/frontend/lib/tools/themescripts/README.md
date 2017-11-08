# themescripts

> The CoreMedia command line interface for building themes.

Install this into your workspace and you'll have access to the `themescripts`.

```shell
npm install file:lib/tools/themescripts
```

The job of the `themescripts` command is to execute npm scripts of all available themes in batch mode.

## Targets

The following targets may be used.

### install

_Run this job with the command `themescripts install`._

This job installs the dependencies of available themes by executing `npm install` in each theme directory. 

### test

_Run this job with the command `themescripts test`._

This job runs all tests of available themes by executing `npm test` in each theme directory. 

### build

_Run this job with the command `themescripts build`._

This job builds all available themes by executing `npm run build` in each theme directory. 
