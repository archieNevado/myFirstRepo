require 'serverspec'
require 'yarjuf'
require 'fileutils'
require 'json'
require 'net/http'
require 'uri'

set :backend, :exec

@chef_node = nil

RSpec.configure do |c|
  FileUtils.mkdir_p '/shared'
  c.output_stream = File.open('/shared/serverspec-reports.xml', 'w')
  c.formatter = 'JUnit'
end

def fetch(uri_str, limit = 10, user = nil, pass = nil)
  fail ArgumentError, 'HTTP redirect too deep' if limit == 0

  uri = URI.parse(uri_str)
  req = Net::HTTP::Get.new(uri.request_uri)
  unless user.nil? && pass.nil?
    req.basic_auth "#{user}", "#{pass}"
    puts "add basic_auth for user #{user}"
  end
  response = Net::HTTP.start(uri.host, uri.port, :use_ssl => uri.scheme == 'https', :verify_mode => OpenSSL::SSL::VERIFY_NONE) { |http| http.request(req) }
  case response
  when Net::HTTPSuccess then
    response
  when Net::HTTPRedirection then
    warn "redirected to #{response['location']}"
    fetch(response['location'], limit - 1, user, pass)
  else
    response
  end
end

def http_response(url, state = 200)
  describe 'Test HTTP Response' do
    it "#{url} respond correctly" do
      r = fetch(url)
      expect(r.code).to eq "#{state}"
    end
  end
end

def basic_login(url, state = 200, user = 'admin', password = 'admin')
  describe 'Test HTTP Response with AUTH' do
    it "#{url} respond correctly" do
      r = fetch(url, 10, user, password)
      expect(r.code).to eq "#{state}"
    end
  end
end

def chef_node
  ::JSON.parse(File.read('/tmp/node.json')) unless @chef_node
end
