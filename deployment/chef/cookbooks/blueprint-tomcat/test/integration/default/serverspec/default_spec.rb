require 'spec_helper'

cae_webapps = %w(cae-preview cae-live-1 cae-live-2)
component_webapps = cae_webapps + %w(caefeeder-preview caefeeder-live content-management-server master-live-server workflow-server user-changes elastic-worker studio content-feeder)
other_webapps = %w(sitemanager solr)
all_webapps = component_webapps + other_webapps

describe 'all services' do
  all_webapps.each do |service_name|
    describe file("/etc/init.d/#{service_name}") do
      it { should be_symlink }
    end
    describe file("/opt/coremedia/#{service_name}/current/bin/setenv.sh") do
      its(:content) { should match(/_jvm_GC_flags_/) }
      its(:content) { should match(/_jvm_agent_opts_/) }
      its(:content) { should match(/_catalina_opts_/) }
    end
  end
end

describe 'component services' do
  %w(cae-preview cae-live-1 caefeeder-preview caefeeder-live content-management-server master-live-server workflow-server user-changes elastic-worker studio solr content-feeder).each do |service_name|
    describe file("/opt/coremedia/#{service_name}/#{service_name}.properties") do
      it { should be_file }
    end
  end
end

describe 'solr' do
  describe file('/opt/coremedia/solr/current/bin/setenv.sh')
  its(:content) { should match(/solr\.solr\.home/) }
end

describe 'cae webapps' do
  describe file("/opt/coremedia/#{service_name}/current/bin/setenv.sh") do
    its(:content) { should match(/_libpeg_turbo_path_/) }
  end
end

describe 'overriding default configuration in cae-live scaling' do
  describe file('/opt/coremedia/cae-live-1/cae-live-1.properties') do
    its(:content) { should match(/_property_default_/) }
  end
  describe file('/opt/coremedia/cae-live-2/cae-live-2.properties') do
    its(:content) { should match(/_property_override_/) }
  end

  describe file('/opt/coremedia/cae-live-1/current/bin/setenv.sh') do
    its(:content) { should match(/_heap_default_/) }
    its(:content) { should match(/_perm_default_/) }
  end
  describe file('/opt/coremedia/cae-live-2/current/bin/setenv.sh') do
    its(:content) { should match(/_heap_override_/) }
    its(:content) { should match(/_perm_override_/) }
  end

  describe file('/opt/coremedia/cae-live-2/current/conf/server.xml') do
    its(:content) { should match(/9999/) }
  end

  describe file('/opt/coremedia/cae-live-2/studio-webapp.war') do
    it { should exist }
  end
end
