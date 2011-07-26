#!/bin/sh

CMD="/tmp/alert_run_${USER}"
tmpfile="/tmp/alert_run_${USER}_tmp_file"

if [ ! -f "$CMD" ]
then
  echo "NOT found"

cat <<_EOF_ > "$CMD"
#!/bin/sh
#DISPLAY=":0.1"

if [ -f ${tmpfile} ]
then
	exit 0
else
	touch ${tmpfile}
	ls ${tmpfile}
fi

\$@ >/dev/null 2>&1  

if [ -f ${tmpfile} ]
then
	rm -f ${tmpfile}
fi

_EOF_

  chmod 755 "$CMD"
fi

exec "$CMD" "$@"