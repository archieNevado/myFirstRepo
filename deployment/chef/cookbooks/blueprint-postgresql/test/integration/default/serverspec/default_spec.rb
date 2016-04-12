require 'spec_helper'

service_name = os[:release].start_with?('7') ? 'postgresql-9.3' : 'postgresql'

describe service(service_name) do
  it { should be_enabled }
  it { should be_running }
end

describe port(5432) do
  it { should be_listening }
end
