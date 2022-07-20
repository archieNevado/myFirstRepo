// Checkmarx assumes a "CGI Stored XSS" issue by printing a System.getenv
// dependent result unencoded.  This is a false positive.  The System.getenv
// values are only credentials and do not affect the result (apart from a
// possible failure due to an authentication error).  And anyway, the result
// is only used for String comparison in shell scripts and never pasted into
// an executable context.
con = com.coremedia.cap.Cap.connect(null, System.getenv("TOOLS_USER"), System.getenv("TOOLS_PASSWORD"));
cr = con.getContentRepository();
ps = cr.getPropertyService();

val = ps.get(System.getProperty("propKey"));

System.out.println((val == null) ? "undefined" : val);

con.close();