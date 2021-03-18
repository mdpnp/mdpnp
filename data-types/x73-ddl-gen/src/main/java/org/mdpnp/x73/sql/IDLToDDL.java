package org.mdpnp.x73.sql;

import java.util.ArrayList;
import java.util.Hashtable;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import ice.IDLBaseListener;
import ice.IDLParser.Constr_type_specContext;
import ice.IDLParser.DefinitionContext;
import ice.IDLParser.Sequence_typeContext;

public class IDLToDDL extends IDLBaseListener {
	
	class StructMember {
		String memberType;
		String memberName;
		
		@Override
		public String toString() {
			return memberName+" - "+memberType;
		}
	}
	
	class Struct {
		String name;
		ArrayList<StructMember> members;
		
		Struct() {
			members=new ArrayList<>();
		}
		
		@Override
		public String toString() {
			StringBuilder sb=new StringBuilder(name);
			for(int i=0;i<members.size();i++) {
				sb.append("\n\t");
				sb.append(members.get(i).toString());
			}
			return sb.toString();
		}
	}
	
	private Hashtable<String,String> typedefs;
	
	private Hashtable<String,Struct> structs;

	public IDLToDDL() {
		typedefs=new Hashtable<>();
		structs=new Hashtable<>();
		//Some "fake" typedefs that are effectively just SQL mappings
		typedefs.put("long", "bigint");
	}

	@Override
	public void enterDefinition(DefinitionContext ctx) {
//		System.err.println("enter definition "+ctx.getText());
		// TODO Auto-generated method stub
		if(ctx.type_decl()!=null) {
			if(ctx.type_decl().getChild(0).getText().equals("typedef")) {
				extractTypedef(ctx.type_decl().getChild(2));
			} else if( ctx.type_decl().getChild(0).getText().equals("struct") ) {
				extractStruct(ctx.type_decl().getChild(0));	
			} else {
//				System.err.printf("enterDefinition not typedef or struct children %d %s\n",ctx.type_decl().getChild(0).getChildCount(),ctx.type_decl().getChild(0).getText());
				if(ctx.type_decl().getChildCount()!=0) {
					recurse(ctx.type_decl());
				}
			}
		}
	}
	
	
	
	/**
	 * Should handle things like struct.
	 * @param parseTree
	 */
	private void recurse(ParseTree parseTree) {
		for(int i=0;i<parseTree.getChild(0).getChildCount();i++) {
//			System.err.printf("recurse child %d is %s\n",i,parseTree.getChild(0).getChild(i).getText());
		}
		ParseTree newRoot=parseTree.getChild(0);
		for(int i=0;i<newRoot.getChildCount();i++) {
			ParseTree pt=newRoot.getChild(i);
			if(pt.getText().equals("struct")) {
				extractStruct(newRoot);
			}
		}
	}

	private void extractTypedef(ParseTree pt) {
		if(pt.getChildCount()!=2) {
			//System.err.println("WARNING typedef with "+pt.getChildCount()+" children");
			return;
		}
		String type=pt.getChild(0).getText();
		String identifier=pt.getChild(1).getText();
		typedefs.put(identifier, type);
		System.err.println("extracted typedef");
	}
	
	private void extractStruct(ParseTree pt) {
		System.err.println("struct parse tree is "+pt.getText());
		String shouldBeStruct=pt.getChild(0).getText();
		if( ! shouldBeStruct.equals("struct")) {
			//System.err.println("extractStruct but root element was "+shouldBeStruct);
			return;
		}
		String structName=pt.getChild(1).getText();
		String shouldBeOpenStruct=pt.getChild(2).getText();
		if(!shouldBeOpenStruct.equals("{")) {
			//System.err.println("extractStruct but 2nd element was "+shouldBeOpenStruct);
			return;
		}
		//int i=3;
		Struct struct=new Struct();
		struct.name=structName;
		ParseTree allStructMembers=pt.getChild(3);
		for(int i=0;i<allStructMembers.getChildCount();i++) {
			System.err.println("structMemberTree "+i+" has "+allStructMembers.getChildCount()+" children and text "+allStructMembers.getText());
			struct.members.add(extractStructMember(allStructMembers,i));
		}
		System.out.println("struct is "+struct.toString());
		structs.put(struct.name, struct);
		
	}
	
	/**
	 * Each struct member should have two elements - type and name.
	 * @param pt
	 */
	private StructMember extractStructMember(ParseTree pt, int i) {
		
		for(int j=0;j<pt.getChild(i).getChildCount();j++) {
			System.err.printf("extractStructMember %d is %s\n",j,pt.getChild(i).getChild(j).getText());
		}
		StructMember ret=new StructMember();
		ret.memberType=pt.getChild(i).getChild(1).getText();
		ret.memberName=pt.getChild(i).getChild(2).getText();
		return ret;
	}
	
	public Hashtable<String,String> getTypedefs() {
		return typedefs;
	}

	@Override
	public void exitDefinition(DefinitionContext ctx) {
		//System.err.println("exit definition "+ctx.getText());
	}
	
	public Hashtable<String, Struct> getStructs() {
		return structs;
	}
	

}
