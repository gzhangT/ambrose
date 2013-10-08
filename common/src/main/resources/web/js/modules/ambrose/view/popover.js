/*
Copyright 2012 Twitter, Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

/**
 * Popover for displaying additional information.
 */
define(['lib/jquery', 'lib/underscore', './core'], function($, _, View) {
    var Popover = View.Popover = function(target, container, data) {
      return new View.Popover.fn.init(target, container, data);
    }

    /**
     * Popover prototype.
     */
    Popover.fn = Popover.prototype = {
      nodedata: undefined,

      target: undefined,

      container: undefined,

      init: function(target, container, data) {
        var self = this;
        self.container = container;
        self.target = target;
        self.nodedata = data;

        this.container.addClass('detailpop');
      },

      setNode: function(data, target, type) {
        self.nodedata = data;
        this.setTarget(target);
        this.updateForNode(type);
      },

      setTarget: function(target) {
          self.target = target;
      },

      // Separate big number with commas.
      commify : function(number) {
        if(isNaN(number)) {
          return number;
        }
        var num = Number(number);
        return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
      },

      updateForNode: function(type) {
        var metricsEl = $('<div">');
        var obj = this;

        var xcor = d3.event.pageX;
        var ycor = d3.event.pageY;

        if (type == 'metrics') {
          this.container.css("width", "230px");
          var headerEl = $('<div class="metric">');
          headerEl.append($('<p class="metric"><b> Metrics </b> </p>'));
          metricsEl.append(headerEl);

          if (self.nodedata.metrics) {
            _.each(self.nodedata.metrics, function(value, key) {
              var metricEl = $('<div class="metric">');
              metricEl.append($('<span class="label">' + key + ':' + '</span>'));
              metricEl.append($('<span class="data">' + ' ' + obj.commify(value) + '</span>'));
              metricsEl.append(metricEl);
            });
          } else {
              var noDataParaEl = $('<div class="metric">');
              noDataParaEl.append($('<p class="metric">Metrics data not available yet. </p>'));
              metricsEl.append(noDataParaEl);
          }
        } else if (type == 'counters') {
           this.container.css("width", "275px");
           var headerEl = $('<div class="metric">');
           headerEl.append($('<p class="metric"><b> File System Counters </b> </p>'));
           metricsEl.append(headerEl);

          if (self.nodedata.counterGroupMap && self.nodedata.counterGroupMap.FileSystemCounters
              && self.nodedata.counterGroupMap.FileSystemCounters.counterInfoMap
              && Object.keys(self.nodedata.counterGroupMap.FileSystemCounters.counterInfoMap).length > 0) {
              var map = self.nodedata.counterGroupMap.FileSystemCounters.counterInfoMap;
              for (var key in map) {
                var metricEl = $('<div class="metric">');
                metricEl.append($('<span class="label">' + map[key].name + ':' + '</span>'));
                metricEl.append($('<span class="data">' + ' ' + obj.commify(map[key].value)
                    + '</span>'));
                metricsEl.append(metricEl);
              }
          } else {
            var noDataParaEl = $('<div class="metric">');
            noDataParaEl.append($('<p class="metric"> Counter Information is not available. </p>'));
            metricsEl.append(noDataParaEl);
          }
        }

        this.container.empty();
        this.updatePosition(xcor, ycor);
        this.addCloseButton(this, this.container.width());
        this.container.append(metricsEl);
      },

      updatePosition: function(xcor, ycor) {
        var targetEl = self.target;

        var screenHeight = $(window).height();
        var screenWidth = $(window).width();

        var position = this.getPosition(targetEl, screenWidth, xcor, ycor);

        this.container.addClass('top');
        this.container.css({top: position.top, left: position.left});
      },

      addCloseButton: function(self, width) {
        var closeEl = $('<div class="closeButton" id="popOverCloseButton">X</div>');
        closeEl.css("margin-left", (width - 25) + "px");
        this.container.append(closeEl);

        document.getElementById('popOverCloseButton').onclick = function() {
          self.hideIfExist();
        }
      },

      getPosition: function(targetEl, winwidth, xcor, ycor) {
        var el = $(targetEl), targetOffset = el.offset(), left, top;

        var bbox = el[0].getBoundingClientRect(),
        width = bbox.width,
        height = bbox.height;

        left = targetOffset.left + width / 2 - this.container.width() / 2;

        // Check boundary to make sure the popover can fit.
        if (left < 0) {
          left = 0;
        } else if (left > (winwidth - this.container.width())) {
          left = winwidth - this.container.width();
        }

        top = Math.max(targetOffset.top + height, 0);

        return {top: ycor, left: xcor};
      },

      show: function() {
        this.container.toggleClass('shown', true);
      },

      cancelHide: function() {
        clearTimeout(this.hideTimeout);
        clearTimeout(this.fadeTimeout);
      },

      hideItem: function() {
        var el = this.container, me = this;
        this.cancelHide();

        this.fadeTimeout = setTimeout(function() {
          //me.target.removeClass('popovered');
          //me.onHide();

          /*
          me.hideTimeout = setTimeout(function() {
            el.hideItem();
          }, 200);
          */
          me.container.toggleClass('shown', false);
        }, 300);
      },

      mouseOutHide: function(newEl) {
        if (newEl) {
          el = $(self.target), newEl = $(newEl);
          if ($.contains(el, newEl) ||  // if newEl is a child element, it's not really out
            newEl.closest('.detailpop').is(this.container)  //  don't hide if mouse moved to popover
          ) {
            return;
          }
        }

        if (!newEl.is(self.target)) {
          this.hideItem();
        }
      },

      hideIfExist: function() {
        this.container.toggleClass('shown', false);
      }
    };

    Popover.fn.init.prototype = Popover.fn;
    return Popover;
});