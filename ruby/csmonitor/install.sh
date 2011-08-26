#!/bin/sh
rsync -avzr --delete --exclude='.idea' --exclude='log*' --exclude='*.log' --exclude="rbcurse" --exclude="tmp" /home/esun/work_bench/hylps_depot/github/Hylps-Utils/ruby/csmonitor /home/esun/depot/config/utils/ruby
