def need_refresh?
  now = Time.now

  if now.sunday? || now.saturday?
    return false
  end

  now_a = now.to_a
  m_start = Time.local(now_a[5], now_a[4], now_a[3], 9, 25)
  m_close = Time.local(now_a[5], now_a[4], now_a[3], 11, 32)
  a_start = Time.local(now_a[5], now_a[4], now_a[3], 12, 59)
  a_close = Time.local(now_a[5], now_a[4], now_a[3], 15, 02)

  if  (m_start <=> now) <= 0 && (now <=> m_close) <= 0
    return true;
  end

  if  (a_start <=> now) <= 0 && (now <=> a_close) <= 0
    return true;
  end

  return false

end


def test_a
  a = %w{aa bb cc}
  b = %w{dd ee ff}

a.push(*b)
  puts a.inspect
end

test_a
puts "hi2"

