The script should be executed in SDE's desktop host (RHEL Linux) as it depends on some development tools. 
To be more specific, the ruby(>= 1.8) and curl are needed by this scripts. 
You can check the availability by following commands. 
$ ruby -v 
$ curl --help 

1. Get the help content. 
$ sh ./image_token_translator.sh -h 

Prep Image Token Translator.
    -v, --verbose                    Turn on verbose output
    -l, --logfile log file           Log file path to be scanned.
    -d, --domain test|prod           Available domains are test for Devo and prod for Prod.
    -h, --help                       Display this usage

2. Examples

2.1) Default domain is test and the Devo camera service is to be used. 
sh ./image_token_translator.sh --logfile ./Sample_log.IMAGE_CAPTURE.log.2014-02-26-21 

2.2) Explicitly specify the domain
sh ./image_token_translator.sh --domain prod --logfile ./Sample_log.IMAGE_CAPTURE.log.2014-02-26-21 

2.3) Verbose output mode
sh ./image_token_translator.sh -v --logfile ./Sample_log.IMAGE_CAPTURE.log.2014-02-26-21 

