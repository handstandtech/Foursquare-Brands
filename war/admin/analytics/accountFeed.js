// Copyright 2009 Google Inc. All Rights Reserved.

/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * @fileoverview Sample program demonstrating how to make a data request to the
 * GA Data Export API using Auth Sub authentication as well as accessing
 * important data in the account feed.
 */
//-----------------------------------------------------------------
// configure GA GData API
//-----------------------------------------------------------------
var myService;
var scope = 'https://www.google.com/analytics/feeds';

/**
 * Initialize the login controls
 */
function init() {
  myService = new google.gdata.analytics.AnalyticsService('gaExportAPI_acctSample_v1.0');
  getStatus();
}

//-----------------------------------------------------------------
// AuthSub Authentication
//-----------------------------------------------------------------
/**
 * Allow user to grant this script access to their GA data.
 */
function login() {
  google.accounts.user.login(scope);
  getStatus();
}

/**
 * Allow user to remove this script's access to their GA data.
 */
function logout() {
  google.accounts.user.logout();
  getStatus();
}

/**
 * Utility function to setup the login/logout functionality.
 */
function getStatus() {
  var status = document.getElementById('status');
  var loginButton = document.getElementById('loginButton');
  if (!google.accounts.user.checkLogin(scope)) {
    loginButton.value = 'Login';
    loginButton.onclick = login;
    status.innerHTML = 'You are logged out, login to continue';
  } else {
    loginButton.value = 'Logout';
    loginButton.onclick = logout;
    status.innerHTML = 'You are logged in';
  }
}

//-----------------------------------------------------------------
// GA Account Feed
//-----------------------------------------------------------------

/**
 * Construct a request for the Account Feed and send to the GA Export API.
 */
function getAccountFeed() {
  var myFeedUri = scope + '/data?ids=ga:40242457&dimensions=ga:eventAction,ga:eventCategory,ga:eventLabel&metrics=ga:pageviews&start-date=2010-12-06&end-date=2010-12-20";

  // Send our request to the Analytics API and wait for the results to come back
  myService.getDataFeed(myFeedUri, handleMyFeed, handleError);
}

/**
 * Handle and display any error that occurs from the API request.
 * @param {Object} e The error object returned by the Analytics API.
 */
function handleError(e) {
  var error = 'There was an error!\n';
  if (e.cause) {
    error += e.cause.status;
  } else {
    error.message;
  }
  alert(error);
}

//-----------------------------------------------------------------
// Format Feed Related Data
//-----------------------------------------------------------------
/**
 * Handle the data the API returns. Then output the data to the screen.
 * @param {Object} myResultsFeedRoot Parameter passed
 *     back from the feed handler.
 */
function handleMyFeed(myResultsFeedRoot) {
  var accountFeed = myResultsFeedRoot.feed;
  var entries = accountFeed.getEntries();
  var displayString = '';
  var tableData = ['<table border="1">'];
  
  alert("data feed:"+accountFeed);

  // Print top-level information about the feed
  displayString = [
    'Feed Title        = ' + accountFeed.getTitle().getText(),
    'Returned Results  = ' + accountFeed.getTotalResults().getValue(),
    'Start Index       = ' + accountFeed.getStartIndex().getValue(),
    'Items Per Page    = ' + accountFeed.getItemsPerPage().getValue(),
    'Feed ID           = ' + accountFeed.getId().getValue()
  ].join('<br/>');
  document.getElementById('outputDiv').innerHTML = displayString;

  //get the feed's entry data
  var row = [
    'WebPropertyId',
    'AccountName',
    'AccountId',
    'Profile Name',
    'ProfileId',
    'Table Id'
  ].join('</th><th>');
  tableData.push('<tr><th>', row, '</th></tr>');

  for (var idx = 0; idx < entries.length; idx++) {
    var entry = entries[idx];
    row = [
      entry.getPropertyValue('ga:webPropertyId'),
      entry.getPropertyValue('ga:AccountName'),
      entry.getPropertyValue('ga:AccountId'),
      entry.getTitle().getText(),
      entry.getPropertyValue('ga:ProfileId'),
      entry.getTableId().getValue()
    ].join('</td><td>');
    tableData.push('<tr><td>', row, '</td></tr>');
  }
  tableData.push('</table>');

  document.getElementById('accountDiv').innerHTML = tableData.join('');
}

// Load the Google data JavaScript client library
google.load('gdata', '1.x', {packages: ['analytics']});

// Set the callback function when the library is ready
google.setOnLoadCallback(init);
