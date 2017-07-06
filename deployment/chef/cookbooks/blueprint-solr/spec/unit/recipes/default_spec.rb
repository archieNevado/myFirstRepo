require 'spec_helper'

describe 'blueprint-solr::default' do
  let(:chef_run) { ChefSpec::SoloRunner.new.converge(described_recipe) }

  it 'includes the java_se recipe' do
    expect(chef_run).to include_recipe('java_se')
  end

  it 'renders the start script' do
    expect(chef_run).to create_template('/var/lib/solr.start')
  end

  it 'renders the init script' do
    expect(chef_run).to create_template('/etc/init.d/solr')
  end

  it 'enables the service' do
    expect(chef_run).to enable_service('solr')
  end

  it 'starts the service' do
    expect(chef_run).to start_service('solr')
  end

  context 'no java_se' do
    let(:chef_run) do
      runner = ChefSpec::SoloRunner.new
      runner.node.set['blueprint']['solr']['install_java'] = false
      runner.converge(described_recipe)
    end

    it 'should not include the java_se recipe' do
      expect(chef_run).to_not include_recipe('java_se')
    end
  end
end
