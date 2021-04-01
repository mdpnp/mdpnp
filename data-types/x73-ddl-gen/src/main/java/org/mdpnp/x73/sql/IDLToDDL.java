package org.mdpnp.x73.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTree;

import ice.IDLBaseListener;
import ice.IDLParser.DefinitionContext;

public class IDLToDDL extends IDLBaseListener {
	private static final List<String> RESERVED = new ArrayList<>(
			Arrays.asList("ACCESSIBLE", "ADD", "ALL", "ALTER", "ANALYZE", "AND", "AS", "ASC", "ASENSITIVE", "BEFORE",
					"BETWEEN", "BIGINT", "BINARY", "BLOB", "BOTH", "BY", "CALL", "CASCADE", "CASE", "CHANGE", "CHAR",
					"CHARACTER", "CHECK", "COLLATE", "COLUMN", "CONDITION", "CONSTRAINT", "CONTINUE", "CONVERT",
					"CREATE", "CROSS", "CURRENT_DATE", "CURRENT_ROLE", "CURRENT_TIME", "CURRENT_TIMESTAMP",
					"CURRENT_USER", "CURSOR", "DATABASE", "DATABASES", "DAY_HOUR", "DAY_MICROSECOND", "DAY_MINUTE",
					"DAY_SECOND", "DEC", "DECIMAL", "DECLARE", "DEFAULT", "DELAYED", "DELETE", "DESC", "DESCRIBE",
					"DETERMINISTIC", "DISTINCT", "DISTINCTROW", "DIV", "DO_DOMAIN_IDS", "DOUBLE", "DROP", "DUAL",
					"EACH", "ELSE", "ELSEIF", "ENCLOSED", "ESCAPED", "EXCEPT", "EXISTS", "EXIT", "EXPLAIN", "FALSE",
					"FETCH", "FLOAT", "FLOAT4", "FLOAT8", "FOR", "FORCE", "FOREIGN", "FROM", "FULLTEXT", "GENERAL",
					"GRANT", "GROUP", "HAVING", "HIGH_PRIORITY", "HOUR_MICROSECOND", "HOUR_MINUTE", "HOUR_SECOND", "IF",
					"IGNORE", "IGNORE_DOMAIN_IDS", "IGNORE_SERVER_IDS", "IN", "INDEX", "INFILE", "INNER", "INOUT",
					"INSENSITIVE", "INSERT", "INT", "INT1", "INT2", "INT3", "INT4", "INT8", "INTEGER", "INTERSECT",
					"INTERVAL", "INTO", "IS", "ITERATE", "JOIN", "KEY", "KEYS", "KILL", "LEADING", "LEAVE", "LEFT",
					"LIKE", "LIMIT", "LINEAR", "LINES", "LOAD", "LOCALTIME", "LOCALTIMESTAMP", "LOCK", "LONG",
					"LONGBLOB", "LONGTEXT", "LOOP", "LOW_PRIORITY", "MASTER_HEARTBEAT_PERIOD",
					"MASTER_SSL_VERIFY_SERVER_CERT", "MATCH", "MAXVALUE", "MEDIUMBLOB", "MEDIUMINT", "MEDIUMTEXT",
					"MIDDLEINT", "MINUTE_MICROSECOND", "MINUTE_SECOND", "MOD", "MODIFIES", "NATURAL", "NOT",
					"NO_WRITE_TO_BINLOG", "NULL", "NUMERIC", "ON", "OPTIMIZE", "OPTION", "OPTIONALLY", "OR", "ORDER",
					"OUT", "OUTER", "OUTFILE", "OVER", "PAGE_CHECKSUM", "PARSE_VCOL_EXPR", "PARTITION", "POSITION",
					"PRECISION", "PRIMARY", "PROCEDURE", "PURGE", "RANGE", "READ", "READS", "READ_WRITE", "REAL",
					"RECURSIVE", "REF_SYSTEM_ID", "REFERENCES", "REGEXP", "RELEASE", "RENAME", "REPEAT", "REPLACE",
					"REQUIRE", "RESIGNAL", "RESTRICT", "RETURN", "RETURNING", "REVOKE", "RIGHT", "RLIKE", "ROWS",
					"SCHEMA", "SCHEMAS", "SECOND_MICROSECOND", "SELECT", "SENSITIVE", "SEPARATOR", "SET", "SHOW",
					"SIGNAL", "SLOW", "SMALLINT", "SPATIAL", "SPECIFIC", "SQL", "SQLEXCEPTION", "SQLSTATE",
					"SQLWARNING", "SQL_BIG_RESULT", "SQL_CALC_FOUND_ROWS", "SQL_SMALL_RESULT", "SSL", "STARTING",
					"STATS_AUTO_RECALC", "STATS_PERSISTENT", "STATS_SAMPLE_PAGES", "STRAIGHT_JOIN", "TABLE",
					"TERMINATED", "THEN", "TINYBLOB", "TINYINT", "TINYTEXT", "TO", "TRAILING", "TRIGGER", "TRUE",
					"UNDO", "UNION", "UNIQUE", "UNLOCK", "UNSIGNED", "UPDATE", "USAGE", "USE", "USING", "UTC_DATE",
					"UTC_TIME", "UTC_TIMESTAMP", "VALUES", "VARBINARY", "VARCHAR", "VARCHARACTER", "VARYING", "WHEN",
					"WHERE", "WHILE", "WINDOW", "WITH", "WRITE", "XOR", "YEAR_MONTH", "ZEROFILL"));
	
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
		typedefs.put("Values", "JSON");
	}

	@Override
	public void enterDefinition(DefinitionContext ctx) {
		if(ctx.type_decl()!=null) {
			if(ctx.type_decl().getChild(0).getText().equals("typedef")) {
				extractTypedef(ctx.type_decl().getChild(2));
			} else if( ctx.type_decl().enum_type() != null ) {
				extractEnum(ctx.type_decl().enum_type());	
			} else if( ctx.type_decl().getChild(0).getText().equals("struct") ) {
				extractStruct(ctx.type_decl().getChild(0));	
			} else {
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
			return;
		}
		String type=pt.getChild(0).getText();
		String identifier=pt.getChild(1).getText();
		identifier = appendIfReserved(identifier);
		typedefs.put(identifier, type);
	}
	
	private void extractEnum(ParseTree pt) {
		String type="varchar(32)";
		String identifier=pt.getChild(1).getText();
		identifier = appendIfReserved(identifier);
		typedefs.put(identifier, type);
	}
	
	private void extractStruct(ParseTree pt) {
		String shouldBeStruct=pt.getChild(0).getText();
		if( ! shouldBeStruct.equals("struct")) {
			return;
		}
		String structName=pt.getChild(1).getText();
		String shouldBeOpenStruct=pt.getChild(2).getText();
		if(!shouldBeOpenStruct.equals("{")) {
			return;
		}
		Struct struct=new Struct();
		struct.name=appendIfReserved(structName);
		ParseTree allStructMembers=pt.getChild(3);
		for(int i=0;i<allStructMembers.getChildCount();i++) {
			struct.members.add(extractStructMember(allStructMembers,i));
		}
		structs.put(struct.name, struct);
	}
	
	/**
	 * Each struct member should have two elements - type and name.
	 * @param pt
	 */
	private StructMember extractStructMember(ParseTree pt, int i) {
		StructMember ret=new StructMember();
		ret.memberType=pt.getChild(i).getChild(1).getText();
		ret.memberName=appendIfReserved(pt.getChild(i).getChild(2).getText());
		return ret;
	}
	
	public Hashtable<String,String> getTypedefs() {
		return typedefs;
	}
	
	public Hashtable<String, Struct> getStructs() {
		return structs;
	}
	
	public boolean isReserved(String name) {
		return RESERVED.contains(name.toUpperCase());
	}
	
	public String appendIfReserved(String identifier) {
		identifier = isReserved(identifier) ? identifier + "_" : identifier;
		return identifier;
	}
}
