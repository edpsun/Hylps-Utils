#!/bin/sh
input_file=$1

deal_with_line(){
	key=`echo $1 |sed "s|\[\(.*\)+.*|\1|g"`
	type=`echo $1 |sed "s|.*\[.*+\(.*\)].*|\1|g"`
	url=`echo $1 | sed "s|.*=\(http\)\(.*\)|\1\2|g"`
	
	if [ "X${key}" = "X" ]
	then
		continue
	fi
	
	cat <<_EOF_
	<condition>
		<name>${key}</name>
		<type>${type}</type>
		<value>
			<![CDATA[
				${url}
			]]>
		</value>
	</condition>
_EOF_


	#echo "$key $type $url"
	
}

cat ${input_file} | while read line; do
	deal_with_line $line
done