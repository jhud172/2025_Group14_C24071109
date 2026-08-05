(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', function () {
    initFeatureTabs();
    initAiDemo();

    function initFeatureTabs() {
      var tabList = document.getElementById('feature-detail-tabs');
      if (!tabList) return;

      var tabs = Array.prototype.slice.call(tabList.querySelectorAll('[role="tab"]'));
      if (!tabs.length) return;

      var activateTab = function (activeTab, moveFocus) {
        tabs.forEach(function (tab) {
          var isActive = tab === activeTab;
          var panelId = tab.getAttribute('aria-controls');
          var panel = panelId ? document.getElementById(panelId) : null;

          tab.setAttribute('aria-selected', isActive ? 'true' : 'false');
          tab.tabIndex = isActive ? 0 : -1;
          if (panel) {
            panel.hidden = !isActive;
          }
        });

        if (moveFocus) {
          activeTab.focus();
        }
      };

      tabList.addEventListener('click', function (event) {
        var tab = event.target.closest('[role="tab"]');
        if (!tab || !tabList.contains(tab)) return;
        activateTab(tab, false);
      });

      tabList.addEventListener('keydown', function (event) {
        var currentTab = event.target.closest('[role="tab"]');
        if (!currentTab || !tabList.contains(currentTab)) return;

        var currentIndex = tabs.indexOf(currentTab);
        var nextIndex = currentIndex;

        if (event.key === 'ArrowRight') {
          nextIndex = (currentIndex + 1) % tabs.length;
        } else if (event.key === 'ArrowLeft') {
          nextIndex = (currentIndex - 1 + tabs.length) % tabs.length;
        } else if (event.key === 'Home') {
          nextIndex = 0;
        } else if (event.key === 'End') {
          nextIndex = tabs.length - 1;
        } else {
          return;
        }

        event.preventDefault();
        activateTab(tabs[nextIndex], true);
      });

      var selectedTab = tabs.find(function (tab) {
        return tab.getAttribute('aria-selected') === 'true';
      }) || tabs[0];
      activateTab(selectedTab, false);
    }

    function initAiDemo() {
      var aiBubble = document.getElementById('aiDemoBubble');
      var aiButtons = Array.prototype.slice.call(document.querySelectorAll('.ai-demo-btn[data-ai-response]'));
      if (!aiBubble || !aiButtons.length) return;

      aiButtons.forEach(function (button) {
        button.addEventListener('click', function () {
          if (button.dataset.aiResponse) {
            aiBubble.textContent = button.dataset.aiResponse;
          }
        });
      });
    }
  });
})();
