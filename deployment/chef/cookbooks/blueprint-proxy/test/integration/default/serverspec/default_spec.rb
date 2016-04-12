require 'spec_helper'
describe 'blueprint-proxy::delivery' do
  chef_node['blueprint']['proxy']['virtual_host']['delivery']['sites'].each_value do |site|
    http_response("http://#{site['server_name']}")
    unless site['server_aliases'].nil?
      site['server_aliases'].each do |alias_name|
        unless alias_name =~ /fragment\.supplier/
          http_response("https://#{alias_name}")
          http_response("http://#{alias_name}")
        end
      end
    end
  end
end

describe 'blueprint-proxy::preview' do
  chef_node['blueprint']['proxy']['virtual_host']['preview']['sites'].each_value do |site|
    http_response("http://#{site['server_name']}")
    http_response("http://#{site['server_name']}/#{chef_node['blueprint']['proxy']['virtual_host']['delivery']['context']}/servlet/catalogimage/10302/en_US/thumbnail/PC_SUMMER_DRESS.jpg")
    http_response("https://#{site['server_name']}/#{chef_node['blueprint']['proxy']['virtual_host']['delivery']['context']}/servlet/catalogimage/10302/en_US/thumbnail/PC_SUMMER_DRESS.jpg")
    http_response("http://#{site['server_name']}/#{chef_node['blueprint']['proxy']['virtual_host']['preview']['context']}/servlet/catalogimage/10302/en_US/thumbnail/PC_SUMMER_DRESS.jpg")
    http_response("https://#{site['server_name']}/#{chef_node['blueprint']['proxy']['virtual_host']['preview']['context']}/servlet/catalogimage/10302/en_US/thumbnail/PC_SUMMER_DRESS.jpg")
    unless site['server_aliases'].nil?
      site['server_aliases'].each do |alias_name|
        unless alias_name =~ /fragment\.supplier/
          http_response("http://#{alias_name}")
          http_response("https://#{alias_name}")
        end
      end
    end
  end
end

describe 'blueprint-proxy::studio' do
  http_response("http://#{chef_node['blueprint']['proxy']['virtual_host']['studio']['server_name']}")
  chef_node['blueprint']['proxy']['virtual_host']['studio']['server_aliases'].each do |alias_name|
    http_response("https://#{alias_name}")
    http_response("http://#{alias_name}")
  end
end

describe 'blueprint-proxy::sitemanager' do
  http_response("http://#{chef_node['blueprint']['proxy']['virtual_host']['sitemanager']['server_name']}")
end

describe 'blueprint-proxy::shop' do
  http_response("http://#{chef_node['blueprint']['proxy']['virtual_host']['shop']['server_name']}")
  http_response("https://#{chef_node['blueprint']['proxy']['virtual_host']['shop']['server_name']}")
end

describe 'blueprint-proxy::shop-preview' do
  http_response("http://#{chef_node['blueprint']['proxy']['virtual_host']['shop-preview']['server_name']}")
  http_response("https://#{chef_node['blueprint']['proxy']['virtual_host']['shop-preview']['server_name']}")
  http_response("http://#{chef_node['blueprint']['proxy']['virtual_host']['shop-preview']['time_travel_alias']}", 403)
  http_response("https://#{chef_node['blueprint']['proxy']['virtual_host']['shop-preview']['time_travel_alias']}", 403)
  unless chef_node['blueprint']['proxy']['virtual_host']['shop-preview']['wcs_tools_alias_map'].nil?
    chef_node['blueprint']['proxy']['virtual_host']['shop-preview']['wcs_tools_alias_map'].each_key do |key|
      http_response("https://#{key}")
    end
  end
end

describe 'blueprint-proxy::default' do
  describe port(80) do
    it { should be_listening }
  end

  describe port(443) do
    it { should be_listening }
  end
end
