package ice;

import java.io.FileInputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import ice.IDLToDDL.StructMember;
import ice.IDLToDDL.Struct;

public class DDLGen {
	
	private static Hashtable<String, String> typedefs;
	private static Hashtable<String, Struct> structs;
	
	private static Pattern stringPattern=Pattern.compile(".?string<(\\d+)>");
	private static Pattern sequencePattern=Pattern.compile("sequence<.*>");

	public static void main(String args[]) {
		
		try {
			ANTLRInputStream input=new ANTLRInputStream(new FileInputStream("/openice/apl3/mdpnp/data-types/x73-idl/src/main/idl/ice/ice.idl"));
			IDLLexer lexer=new IDLLexer(input);
			CommonTokenStream tokens=new CommonTokenStream(lexer);
			IDLParser parser=new IDLParser(tokens);
			ParseTree tree=parser.definition();
			//System.out.println(tree.toStringTree(parser));
			ParseTreeWalker walker=new ParseTreeWalker();
			IDLToDDL listener=new IDLToDDL();
			walker.walk(listener, tree);
			System.err.println();
			System.out.println();
			typedefs=listener.getTypedefs();
			System.err.println("typedefs is "+typedefs);
			structs=listener.getStructs();
			
			generateDDL();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void generateDDL() {
		StringBuilder ddl=new StringBuilder();
		for(String s : structs.keySet()) {
			Struct struct=structs.get(s);
			ddl.append("CREATE TABLE "+struct.name+" (\n");
			for(int i=0;i<struct.members.size();i++) {
				StructMember member=struct.members.get(i);
				//We need somehow to deal with "compound" types, where the type
				//is already some other struct like image.
				if( structs.containsKey(member.memberType) ) {
					Struct otherStruct=structs.get(member.memberType);
					for(int j=0;j<otherStruct.members.size();j++) {
						StructMember otherMember=otherStruct.members.get(j);
						ddl.append("\t"+member.memberName+"_");
						ddl.append(otherMember.memberName);
						ddl.append(" "+ decodeType(otherMember.memberType));
						if(i<struct.members.size()-1) {
							//There will definitely be something else.
							ddl.append(",\n");
						} else if (j<otherStruct.members.size()-1) {
							//There will definitely be something else.
							ddl.append(",\n");
						} else {
							ddl.append("\n");
						}
					}
					//ddl.append( i<struct.members.size()-1 ? ",\n" : "\n");
				} else {
					ddl.append("\t"+member.memberName+" ");
					ddl.append( decodeType(member.memberType) );
					ddl.append( i<struct.members.size()-1 ? ",\n" : "\n");
					//ddl.append(",\n");
				}
				
			}
			ddl.append(");\n");
		}
		System.out.println(ddl);
	}
	
	private static String decodeType(String type) {
		String idlType=type;
		while(typedefs.containsKey(idlType)) {
			idlType=typedefs.get(idlType);
		}
		Matcher m=stringPattern.matcher(idlType);
		if(m.matches()) {
			idlType="varchar("+m.group(1)+")";	//group(1) is the NNN in <NNN>
		}
		m=sequencePattern.matcher(idlType);
		if(m.matches()) {
			idlType="blob";
		}
		
		return idlType;
	}

	


}