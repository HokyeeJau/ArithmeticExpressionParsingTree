import com.sun.source.tree.Tree
import java.util.regex.Matcher

/**
 * @param leftNode: Left Subsidy
 * @param type: data type, operator -> 0, number -> 1
 * @param value: Token
 * @param rightNode: Right Subsidy
 * @param parent: Parent Index
 */

data class TreeNode(var parent: TreeNode? = null, var leftNode: TreeNode? = null,
                    var rightNode: TreeNode? = null, var value: String? = null, var type:Int? = 1)
data class ParsingTree(var rootNode: TreeNode) {}
val oprArr = arrayOf<String>("+", "-", "*", "\\")

// Build Parsing Tree
fun buildTree(tokens:Array<String>): ParsingTree? {

    val numPtn = Regex("""\d+(\.?\d*)?""")
    val optPtn = Regex("""[*/\-+]""")
    var source = Array<String>(tokens.count(), {i->"a"})

    // If the first token is operator
    if ("-" in tokens[0] || "+" in tokens[0]) {
        source = Array<String>(tokens.count()+1, {i->"a"})
        source[0] = "0"
        System.arraycopy(tokens, 0, source, 1, tokens.count())
    } else if("*" in tokens[0] || "/" in tokens[0]) {
        return null
    } else {
        source = tokens
    }

    // Set up root, current node
    var root: ParsingTree ?= null
    var current: TreeNode ?= null
    current = TreeNode(parent=null, leftNode = null, rightNode = null, value=source[0], type=1)
    root = ParsingTree(current)

    // Extend Branches
    for(index in 1..source.count()-1) {
        // if the token is operator
        if (optPtn.containsMatchIn(source[index])){
            if("*" in source[index] || "/" in source[index]) {
                // if the token is multiplication or subtraction

                // New a node
                var opt: TreeNode = TreeNode(current?.parent, null, null, source[index], 0)
                // If parent is rootNode
                if (current?.parent==null) {
                    root = ParsingTree(opt)
                } else {
                    current.parent?.rightNode = opt
                }
                opt.leftNode = current
                current?.parent = opt
                current = opt
            } else if("+" in source[index] || "-" in source[index]) {
                // if the token is minus or plus

                var opt: TreeNode = TreeNode(null, root?.rootNode, null, source[index], 0)
                root?.rootNode?.parent = opt
                root?.rootNode = opt
                current = opt
            }
        } else {
            // if the token is number
            var num: TreeNode = TreeNode(current, null, null, source[index], 1)
            current?.rightNode = num
            current = num
        }
    }
    return root
}

// Print Parsing Tree
fun printTree(node: TreeNode) {
    node.leftNode?.let{
        it -> printTree(it)
    }
    println(node.value)
    node.rightNode?.let{
        it -> printTree(it)
    }
}

// Calculate Parsing Tree
fun calTree(node: TreeNode): Double? {
    if (node.type==0) {
        var left: Double? = node.leftNode?.let{calTree(it)}
        var right: Double? = node.rightNode?.let { calTree(it) }
        if (node.value == "+") {
            return left!! + right!!
        } else if (node.value == "-") {
            return left!! - right!!
        } else if(node.value == "*") {
            return left!! * right!!
        } else {
            return left!! / right!!
        }
    } else {
        return node.value?.toDouble()
    }
}
// Identify all the tokens included in the arithmetic expression
fun identifyAllTokens(rsc: String): Array<String> {
    val source = rsc
    val pattern = """\d+(\.?\d*)?|<\d+(\.?\d*)?>|[+\-*/]"""
    val res = Regex(pattern).findAll(source).toList()
    var arr = Array<String>(res.count(), {i->"a"})
    for(index in 0..res.count()-1) {
        var temp: String?=null
        if('<' in res[index].value){
            var t = Regex("\\d+(\\.?\\d*)?").find(res[index].value)?.value?.toDouble()
            temp = t?.let { Math.sqrt(it).toString() }
        } else {
            temp = res[index].value
        }

        if (temp != null) {
            arr[index] = temp
        }
    }
    return arr
}

fun main(args: Array<String>) {
    val source = "-9.106+3.4*9/45-0.01+<2.78>"

    var tokens = identifyAllTokens(source)
    var root = buildTree(tokens)
    if (root is ParsingTree) {
//        printTree(root.rootNode)
        println("Result: "+ calTree(root.rootNode).toString())
    }

}