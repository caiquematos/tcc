<?php

/*
|--------------------------------------------------------------------------
| Application Routes
|--------------------------------------------------------------------------
|
| Here is where you can register all of the routes for an application.
| It's a breeze. Simply tell Laravel the URIs it should respond to
| and give it the Closure to execute when that URI is requested.
|
*/

if (Session::get("user") == null) {
  
  Route::get('/', function() {
      return View::make("login");
  });
  
  Route::controller('/user', 'UserController');
  Route::controller('/coordinator', 'CoordinatorController');
  Route::controller('/module', 'ModuleController');
  Route::controller('/gcm', 'GCMController');
  Route::controller('/history', 'HistoryController');
} else {
  
  Route::get('/logout', function() {
    Session::flush();
    return Redirect::guest("/")->with("You have logged out");
  });  
  
  Route::get(urlencode('module?coordinator={coordinator}'), array('as'=>'module', 'uses'=>'ModuleController@getIndex'));
  Route::controller('/module', 'ModuleController');
  Route::controller('/coordinator', 'CoordinatorController');
  Route::controller('/', 'CoordinatorController');
}
