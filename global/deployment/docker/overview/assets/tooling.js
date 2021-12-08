var checkStatus = function() {
  $(".health_check").each(function() {
    var $that = $(this);
    var $status = $that.parents("tr").find("span");
    fetch($that.attr("href")).then(function(response) {
      if (response.status === 200) {
        return response.json();
      } else {
        return false;
      }
    }).then(function(data) {
      if (data.status === "UP") {
        $status.removeClass().addClass("check_ok");
        $status.text("Healthy");
      } else {
        $status.removeClass().addClass("check_fail");
        $status.text("Unhealthy");
      }
    }).catch(function() {
      $status.removeClass().addClass("check_fail");
      $status.text("Unhealthy");
    });
  });
};

// dom ready
$(function() {
  // check docker health every 20 seconds
  checkStatus();
  var checkInterval = setInterval(checkStatus, 20000);

  // copy code to clipboard
  $("code").on("click", function() {
    var $codeBlock = $(this);
    $codeBlock.addClass("selected");
    var el = document.createElement("textarea");
    el.value = this.innerText;
    el.setAttribute("readonly", "");
    el.style.position = "absolute";
    el.style.left = "-9999px";
    document.body.appendChild(el);
    var selected =  document.getSelection().rangeCount > 0 ? document.getSelection().getRangeAt(0) : false;
    el.select();
    document.execCommand("copy");
    document.body.removeChild(el);
    if (selected) {
      document.getSelection().removeAllRanges();
      document.getSelection().addRange(selected);
    }
    setTimeout(function() { $codeBlock.removeClass("selected"); }, 100);
  });

  // fix docker links, if not set by confd
  $("a[href*=\"docker.localhost\"]").each(function() {
    console.warn("Overview includes docker URL.", $(this).attr("href"));
    $(this).attr("href", $(this).attr("href").replace("docker.localhost", window.location.hostname.replace("overview.", "")));
  });

  // change log level
  // part a) get list of loggers for service
  var $messageBox = $("#logger-message");
  var $loggers = $("#loggers");
  var availableLoggers = {};
  $("#service").on("change", function() {
    $loggers.empty();
    var service = $(this).val();
    if(service !== undefined) {
      var url = '/'.concat(service).concat('/loggers/');
      fetch(url).then(function(response) {
        return response.json();
      }).then(function(data) {
        availableLoggers = Object.keys(data.loggers);
        if(availableLoggers.length > 0) {
          $messageBox.html("<b>" + availableLoggers.length + "</b> loggers found for service <b>" + service + "</b>");
          availableLoggers.forEach(function(loggerName) {
            $loggers.append($("<option>", { value: loggerName}))
          })
        } else {
          $messageBox.html("<em>Error:</em> No loggers found for service <b>" + service + "</b>");
        }
      }).catch(function() {
        $messageBox.html("<em>Error:</em> Could not fetch loggers for service <b>" + service + "</b>");
      });
    }
  });
  //part b) set new logger log level
  $("#log-level-change").on("submit", function(e) {
    e.preventDefault();
    var formData = new FormData(this);
    var url = '/'.concat(formData.get('service')).concat('/loggers/').concat(formData.get('logger'));
    $messageBox.text("Setting new log level for logger " + formData.get('logger') + "...");
    fetch(url, {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({'configuredLevel': formData.get('level')}),
    }).then(function(postResponse) {
      if(postResponse.ok === true) {
        fetch(url).then(function(response) {
          return response.json();
        }).then(function(data) {
          if (data.effectiveLevel !== undefined) {
            $messageBox.html("The log level for <b>" + formData.get('service') + "</b> and logger <b>" + formData.get('logger') + "</b> successfully changed to <b>" + data.effectiveLevel + "</b>");
          } else {
            $messageBox.html("<em>Error:</em> Could not set log level for <b>" + formData.get('service') + "</b> and logger <b>" + formData.get('logger') + "</b>");
          }
        });
      } else {
        $messageBox.html("<em>Error:</em> Could not set log level for <b>" + formData.get('service') + "</b> and logger <b>" + formData.get('logger') + "</b>");
      }
    }).catch(function() {
      $messageBox.html("<em>Error:</em> Could not set log level for <b>" + formData.get('service') + "</b> and logger <b>" + formData.get('logger') + "</b>");
    });
  });
});
