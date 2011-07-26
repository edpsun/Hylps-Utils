#!/bin/sh
rsync -avzr --delete --exclude='.idea' --exclude='log*' --exclude='*.log' --exclude="rbcurse" --exclude="tmp"  /export/work/workbench/rubymine/csmonitor /home/esun/depot/config/utils/ruby
