var JSRuleEngine = function(){
	var rules = {};
	var ruleDictionary = {};
	var result = {
		ruleName: null,
		success: false,
		messages: []
	};
	return {
		isLoaded: function(rule){
			return typeof rules[rule.versionedName] === "function";
		},
		addRule: function(rule,reference){
			ruleDictionary[rule.versionedName] = rule;
			rules[rule.versionedName] = reference;
			
		},
		fire: function(rule,fact){
			result.ruleName = rule.name;
			result.ruleVersion = rule.version;
			var r = null;
			try {
				if(rules[rule.versionedName]!==undefined){
					r = rules[rule.versionedName](fact);
//					print("Fired!");
					result.success = true;
					if(rule.type == 'DECISION'){
						result.success = r;
					}else
					if(rule.type == 'MUTATION'){
//						if(typeof r == 'object')
//							parse = true;
						//result["payload"] = parse?JSON.parse(r):r;
					}else{
						//rule type not recognized
						print("Rule type not recognized ("+rule.type+")");
						result.messages.push("Rule type \""+rule.type+"\" not recognized");
					}
				}else{
					result.messages.push("Rule \""+rule.name+"\" not published");
				}
			} catch (e) {
				var parse = false;
				if(typeof e == 'object')
					parse = true;
				result.messages.push((parse?JSON.parse(e):e));
			}
			var response = JSON.stringify(result);
			return response;
		}
	}
};