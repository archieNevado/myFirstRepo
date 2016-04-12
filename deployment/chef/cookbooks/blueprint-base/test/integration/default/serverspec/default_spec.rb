require 'spec_helper'

describe file('/opt/coremedia') do
  it { should be_directory }
  it { should be_owned_by 'coremedia' }
  it { should be_grouped_into 'coremedia' }
end

describe user('coremedia') do
  it { should exist }
  it { should belong_to_group 'coremedia' }
  it { should have_home_directory '/opt/coremedia' }
end
