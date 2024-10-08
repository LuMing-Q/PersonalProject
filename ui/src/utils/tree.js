/**  平面数据转树形结构
 *   z存在父级id 代表对应的数据有children，需要Push进去
 */

export const buildTree = ( treeArray, id, pid) => {
    var r = [];
    var tmpMap = {};
    for (var i = 0, l = treeArray.length; i < l; i++) {
			// 以每条数据的id作为obj的key值，数据作为value值存入到一个临时对象里面
			tmpMap[treeArray[i][id]] = treeArray[i];
    }
  
    for (i = 0, l = treeArray.length; i < l; i++) {
			var key = tmpMap[treeArray[i][pid]];

			//循环每一条数据的pid，假如这个临时对象有这个key值，就代表这个key对应的数据有children，需要Push进去
			if (key) {
					if (!key["children"]) {
							key["children"] = [];
							key["children"].push(treeArray[i]);
					} else {
							key["children"].push(treeArray[i]);
					}
			} else {
					//如果没有这个Key值，那就代表没有父级,直接放在最外层
					r.push(treeArray[i]);
			}
    }
    return r
  }
  export default {
    buildTree
  };
  