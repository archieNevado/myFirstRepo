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
  });
});
