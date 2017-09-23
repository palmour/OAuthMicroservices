/**
 * 
 */

angular.module("app", []).controller("index", function($http){
	var self = this;
	
	$http.get("/user").then(function(data){
		console.log(data);
		self.authenticated = true;
	}).catch(function(data){
		console.log(data);
	});
	
	self.logout = function(){
		$http.post("/logout", {}).then(function(response){
			console.log(response);
		});
	};
});