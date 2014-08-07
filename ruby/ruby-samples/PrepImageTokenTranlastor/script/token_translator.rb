CURL_CMD_TEMPLATE = "curl --silent --show-error 'https://%s/'  -H 'Content-Encoding: amz-1.0' -H 'Origin: https://cas-prod-na.amazon.com' -H 'Accept-Encoding: gzip,deflate,sdch' -H 'Accept-Language: en-US,en;q=0.8,zh-CN;q=0.6' -H 'X-Amz-Target: com.amazon.aft.vision.cameraaccess.CameraAccessService.RetrieveImage' -H 'X-Requested-With: XMLHttpRequest' -H 'Connection: keep-alive' -H 'Pragma: no-cache' -H 'Content-Type: application/json; charset=UTF-8' -H 'X-Amz-Nonce: 8A11FFF8-B152-4635-87EC-B4876B7457DF' -H 'Accept: application/json, text/javascript, */*' -H 'Cache-Control: no-cache' -H 'x-amz-requestsupertrace: true' --compressed --insecure --data-binary '{\"imageId\": \"%s\"}' "

PATTERN_SUCC = Regexp.new('.*mrl.*"uri":"(.*)"\}\}')
PATTERN_FAIL = Regexp.new('\{"__type":".*#(.*)",".*\}')
class TokenTranslator
  def translate(token)
    cmd = sprintf CURL_CMD_TEMPLATE, $options[:host], token
    url = nil
    begin
      res = nil
      IO.popen(cmd) {|f| res =f.gets}
      url = parse_response(res)
    rescue Exception
      puts "[ERROR] error msg: #{$!} at: #{$@}"
      url = "Error while retrieving the URL for given token."
      add_failure
    end
    url = '[ERROR] Run into unexpected Exception. Please refer to console log.' if !url
    return url
  end

  def parse_response(res)
    md = PATTERN_SUCC.match(res)

    if md
      md[1]
    else
      add_failure
      md2 = PATTERN_FAIL.match(res)
      puts "[ERROR] Run into error. Msg: #{res}" if $options[:verbose]
      if md2
        md2[1]
      else
        res
      end
    end
  end

  private
  def add_failure
    if $counters.failures
      $counters.failures += 1
    else
      $counters.failures = 1
    end
  end

end