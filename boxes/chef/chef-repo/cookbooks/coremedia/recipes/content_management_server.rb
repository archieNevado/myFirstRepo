coremedia_tool "cm7-cms-tools"

coremedia_service "cm7-cms-tomcat"

coremedia_logging "cm7-cms-tomcat" do
  webapps %w(coremedia contentfeeder user-changes)
end

coremedia_probedog "cm7-cms-tools" do
  action :nothing
  probe "ProbeContentServerOnline"
  timeout node["coremedia"]["probedog"]["timeout"]
  subscribes :check, "service[cm7-cms-tomcat]", :delayed
  subscribes :check, "service[cm7-cms-tomcat_restart]", :delayed
end

working_directory = "#{node["coremedia"]["install_root"]}/data"

directory working_directory do
  unless platform_family?("windows")
    owner node["coremedia"]["user"]
    group node["coremedia"]["user"]
  end
  recursive true
end

unless platform_family?("windows")
  package "unzip" do
    retries node["coremedia"]["package"]["retries"]
  end
end

coremedia_content "unpack-content" do
  action :unpack
  archive node["coremedia"]["content_archive"]
  working_dir working_directory
  not_if { node["coremedia"]["content_archive"].empty? }
end

coremedia_content "import-content" do
  action :nothing
  working_dir working_directory
  cms_tools "#{node["coremedia"]["install_root"]}/cm7-cms-tools"
  subscribes :import, "coremedia_content[unpack-content]", :delayed
end

# this resource execution implies that the master_live_server recipe is executed before the content_management_server recipe
coremedia_content "publish-content" do
  action :nothing
  ignore_failure true
  cms_tools "#{node["coremedia"]["install_root"]}/cm7-cms-tools"
  subscribes :publishall, "coremedia_content[import-content]", :delayed
end
