require 'spec_helper'

describe yumrepo('mongodb-org-3.2') do
  it { should exist }
  it { should be_enabled }
end

describe package('mongodb-org-server') do
  it { should be_installed } # .with_version('3.2.5') }
end

describe package('mongodb-org-shell') do
  it { should be_installed } # .with_version('3.2.5') }
end

describe package('mongodb-org-tools') do
  it { should be_installed } # .with_version('3.2.5') }
end

describe file('/etc/init.d/disable-thp') do
  it { should be_file }
  it { should be_owned_by 'root' }
  it { should be_grouped_into 'root' }
  it { should be_mode 755 }
end

describe file('/var/log/mongodb') do
  it { should be_directory }
  it { should be_owned_by 'mongod' }
  it { should be_grouped_into 'mongod' }
  it { should be_mode 755 }
end

describe file('/var/run/mongodb') do
  it { should be_directory }
  it { should be_owned_by 'mongod' }
  it { should be_grouped_into 'mongod' }
  it { should be_mode 755 }
end

describe service('disable-thp') do
  it { should be_enabled.with_level(3) }
end

describe service('mongod') do
  it { should be_enabled.with_level(3) }
  it { should be_running }
end

describe port(27_017) do
  it { should be_listening.on('0.0.0.0').with('tcp') }
end

describe port(28_017) do
  it { should be_listening.on('0.0.0.0').with('tcp') }
end
