package org.mdpnp.transport.dds.rti;

import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mdpnp.comms.IdentifiableUpdate;
import org.mdpnp.comms.Identifier;
import org.mdpnp.comms.MutableIdentifiableUpdate;
import org.mdpnp.comms.Persistent;
import org.mdpnp.transport.dds.rti.RTICLibrary.DDS_Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.DoubleByReference;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.ptr.ShortByReference;

public class DDSInterface {

	// TOTALLY ARBITRARY EXTENT VALUES
	public static final int OCTET_SEQUENCE_EXTENT = 1 << 20;
	public static final int DOUBLE_SEQUENCE_EXTENT = 1 << 10;
	public static final int STRING_EXTENT = 4096;

	public DDSInterface() {
		if (0 == RTICLibrary.INSTANCE.DDS_OctetSeq_initialize(readOctetSequence
				.getPointer())) {
			throw new RuntimeException("failed to initialize DDS_OctetSeq");
		}
		readOctetSequence.read();
		if (0 == RTICLibrary.INSTANCE
				.DDS_OctetSeq_initialize(writeOctetSequence.getPointer())) {
			throw new RuntimeException("failed to initialize DDS_OctetSeq");
		}
		writeOctetSequence.read();
		if (0 == RTICLibrary.INSTANCE
				.DDS_DoubleSeq_initialize(readDoubleSequence.getPointer())) {
			throw new RuntimeException("failed to initialize DDS_DoubleSeq");
		}
		readDoubleSequence.read();
		if (0 == RTICLibrary.INSTANCE
				.DDS_DoubleSeq_initialize(writeDoubleSequence.getPointer())) {
			throw new RuntimeException("failed to initialize DDS_DoubleSeq");
		}
		writeDoubleSequence.read();
	}

	public final void arrayToSequence(byte[] bytes, RTICLibrary.DDS_Sequence seq) {
		if (0 == RTICLibrary.INSTANCE.DDS_OctetSeq_initialize(seq.getPointer())) {
			throw new RuntimeException("Unable to initialize sequence");
		}
		if (0 == RTICLibrary.INSTANCE.DDS_OctetSeq_ensure_length(
				seq.getPointer(), bytes.length, bytes.length)) {
			throw new RuntimeException("Unable to ensure_length");
		}

		for (int i = 0; i < bytes.length; i++) {
			Pointer p = RTICLibrary.INSTANCE.DDS_OctetSeq_get_reference(
					seq.getPointer(), i);
			if (null == p) {
				throw new RuntimeException("Unable to get_reference");
			}
			p.setByte(0, bytes[i]);
		}
		seq.read();
	}

	public final void arrayToSequence(Number[] numbers,
			RTICLibrary.DDS_Sequence seq) {
		// if(0 ==
		// RTICLibrary.INSTANCE.DDS_DoubleSeq_initialize(seq.getPointer())) {
		// throw new RuntimeException("Unable to initialize sequence");
		// }
		if (0 == RTICLibrary.INSTANCE.DDS_DoubleSeq_ensure_length(
				seq.getPointer(), numbers.length, numbers.length)) {
			throw new RuntimeException("Unable to ensure_length");
		}
		for (int i = 0; i < numbers.length; i++) {
			Pointer p = RTICLibrary.INSTANCE.DDS_DoubleSeq_get_reference(
					seq.getPointer(), i);
			if (null == p) {
				throw new RuntimeException("Unable to get_reference");
			}
			if (null == numbers[i]) {
				p.setDouble(0, 0);
			} else {
				p.setDouble(0, numbers[i].doubleValue());
			}
		}
		seq.read();
	}

	public final void arrayToSequence(String[] strings,
			RTICLibrary.DDS_Sequence seq) {
		if (0 == RTICLibrary.INSTANCE
				.DDS_StringSeq_initialize(seq.getPointer())) {
			throw new RuntimeException("Unable to initialize sequence");
		}
		if (0 == RTICLibrary.INSTANCE.DDS_StringSeq_ensure_length(
				seq.getPointer(), strings.length, strings.length)) {
			throw new RuntimeException("Unable to ensure_length");
		}
		for (int i = 0; i < strings.length; i++) {
			Pointer p = RTICLibrary.INSTANCE.DDS_StringSeq_get_reference(
					seq.getPointer(), i);
			if (null == p) {
				throw new RuntimeException("Unable to get_reference");
			}
			// TODO think about string lifecycles (among other things)
			if (null == strings[i]) {
				p.setPointer(0, null);
			} else {
				p.setPointer(0, RTICLibrary.INSTANCE.DDS_String_dup(strings[i]));
			}
		}
		seq.read();
	}

	private static final String _checkReturnCode(int code) {
		switch (code) {
		case RTICLibrary.DDS_RETCODE_OK:
			return null;
		case RTICLibrary.DDS_RETCODE_ERROR:
			return "DDS_RETCODE_ERROR";
		case RTICLibrary.DDS_RETCODE_UNSUPPORTED:
			return "DDS_RETCODE_UNSUPPORTED";
		case RTICLibrary.DDS_RETCODE_BAD_PARAMETER:
			return "DDS_RETCODE_BAD_PARAMETER";
		case RTICLibrary.DDS_RETCODE_PRECONDITION_NOT_MET:
			return "DDS_RETCODE_PRECONDITION_NOT_MET";
		case RTICLibrary.DDS_RETCODE_OUT_OF_RESOURCES:
			return "DDS_RETCODE_OUT_OF_RESOURCES";
		case RTICLibrary.DDS_RETCODE_NOT_ENABLED:
			return "DDS_RETCODE_NOT_ENABLED";
		case RTICLibrary.DDS_RETCODE_IMMUTABLE_POLICY:
			return "DDS_RETCODE_IMMUTABLE_POLICY";
		case RTICLibrary.DDS_RETCODE_INCONSISTENT_POLICY:
			return "DDS_RETCODE_INCONSISTENT_POLICY";
		case RTICLibrary.DDS_RETCODE_ALREADY_DELETED:
			return "DDS_RETCODE_ALREADY_DELETED";
		case RTICLibrary.DDS_RETCODE_TIMEOUT:
			return "DDS_RETCODE_TIMEOUT";
		case RTICLibrary.DDS_RETCODE_NO_DATA:
			return "DDS_RETCODE_NO_DATA";
		case RTICLibrary.DDS_RETCODE_ILLEGAL_OPERATION:
			return "DDS_RETCODE_ILLEGAL_OPERATION";
		default:
			return "Unknown return code:" + code;
		}
	}

	public static final boolean checkReturnCodeForNoData(int code) {
		if (RTICLibrary.DDS_RETCODE_NO_DATA == code) {
			return true;
		}
		String s = _checkReturnCode(code);
		if (null != s) {
			throw new RuntimeException(s);
		} else {
			return false;
		}
	}

	public static final void checkReturnCode(int code) {
		String s = _checkReturnCode(code);
		if (null != s) {
			throw new RuntimeException(s);
		}
	}

	private static final String _checkException(int code) {
		switch (code) {
		case RTICLibrary.DDS_NO_EXCEPTION_CODE:
			return null;
		case RTICLibrary.DDS_USER_EXCEPTION_CODE:
			return "DDS_USER_EXCEPTION_CODE";
		case RTICLibrary.DDS_SYSTEM_EXCEPTION_CODE:
			return "DDS_SYSTEM_EXCEPTION_CODE";
		case RTICLibrary.DDS_BAD_PARAM_SYSTEM_EXCEPTION_CODE:
			return "DDS_BAD_PARAM_SYSTEM_EXCEPTION_CODE";
		case RTICLibrary.DDS_NO_MEMORY_SYSTEM_EXCEPTION_CODE:
			return "DDS_NO_MEMORY_SYSTEM_EXCEPTION_CODE";
		case RTICLibrary.DDS_BAD_TYPECODE_SYSTEM_EXCEPTION_CODE:
			return "DDS_BAD_TYPECODE_SYSTEM_EXCEPTION_CODE";
		case RTICLibrary.DDS_BADKIND_USER_EXCEPTION_CODE:
			return "DDS_BADKIND_USER_EXCEPTION_CODE";
		case RTICLibrary.DDS_BOUNDS_USER_EXCEPTION_CODE:
			return "DDS_BOUNDS_USER_EXCEPTION_CODE";
		case RTICLibrary.DDS_IMMUTABLE_TYPECODE_SYSTEM_EXCEPTION_CODE:
			return "DDS_IMMUTABLE_TYPECODE_SYSTEM_EXCEPTION_CODE";
		case RTICLibrary.DDS_BAD_MEMBER_NAME_USER_EXCEPTION_CODE:
			return "DDS_BAD_MEMBER_NAME_USER_EXCEPTION_CODE";
		case RTICLibrary.DDS_BAD_MEMBER_ID_USER_EXCEPTION_CODE:
			return "DDS_BAD_MEMBER_ID_USER_EXCEPTION_CODE";
		default:
			return "UNKNOWN ERROR";
		}

	}

	public static final void checkException(IntByReference mem) {

		String err = _checkException(mem.getValue());
		if (null != err) {
			throw new RuntimeException(err);
		}
	}

	protected final void checkReadException() {
		checkException(readException);
	}

	protected final void checkWriteException() {
		checkException(writeException);
	}

	public void get(Pointer dd, MutableIdentifiableUpdate<?> update)
			throws NoSuchMethodException, SecurityException,
			IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		for (DDSInterface.Getter m : populateGetters(update.getClass())
				.getGetters()) {

			Method setter = m.getSetter();
			if (null == setter) {
				setter = update.getClass().getMethod(
						"s" + m.getName().substring(1, m.getName().length()),
						m.getReturnType());
				m.setSetter(setter);
			}
			setter.invoke(
					update,
					get(m.getName(), dd, setter.getParameterTypes()[0],
							m.getMemberId(), m.getKind()));
		}
	}

	private static final Logger log = LoggerFactory
			.getLogger(DDSInterface.class);
	private final IntByReference readException = new IntByReference();
	private final IntByReference writeException = new IntByReference();

	private final Date date = new Date();

	private final LongByReference longByReference = new LongByReference();
	private final IntByReference intByReference = new IntByReference();
	private final ShortByReference shortByReference = new ShortByReference();
	private final FloatByReference floatByReference = new FloatByReference();
	private final DoubleByReference doubleByReference = new DoubleByReference();
	private final ByteByReference byteByReference = new ByteByReference();

	
	// TODO change the API to specify the length of things externally?
	
	private Map<Integer, SoftReference<Number[]>> numbers = new HashMap<Integer, SoftReference<Number[]>>();
	private Map<Integer, SoftReference<byte[]>> octets = new HashMap<Integer, SoftReference<byte[]>>();
	
	private final Number[] getNumbersByLength(int length) {
		SoftReference<Number[]> ref = this.numbers.get(length);
		Number[] numbers = null == ref ? null : ref.get();
		
		if(null == numbers) {
			numbers = new Number[length];
			this.numbers.put(length, new SoftReference<Number[]>(numbers));
		}
		return numbers;
	}
	
	private final byte[] getOctetsByLength(int length) {
		SoftReference<byte[]> ref = this.octets.get(length);
		byte[] octets = null == ref ? null : ref.get();
		
		if(null == octets) {
			octets = new byte[length];
			this.octets.put(length, new SoftReference<byte[]>(octets));
		}
		return octets;
	}

	
	public final Object get(String name, Pointer dd, Class<?> tgt,
			int member_id, int kind) {
		if (RTICLibrary.DDS_TK_STRING == kind) {
			strlen.setValue((int) (stringMemory.size() - 1));

			PointerByReference pbr = new PointerByReference(stringMemory);
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_string(dd, pbr, strlen, null,
							member_id))) {
				return null;
			}

			if (0 >= strlen.getValue()) {
				log.debug("string retrieved with length " + strlen.getValue());
			}
			pbr.getValue().setByte(strlen.getValue(), (byte) 0);
			String s = stringMemory.getString(0);

			if (null == s) {
				return null;
			}
			if (Enum.class.isAssignableFrom(tgt)) {
				int idx = s.lastIndexOf('.');
				if (idx < 0) {
					log.error("No dots in the enum named " + s);
					return null;
				}
				Class<?> enumClass;
				try {
					enumClass = Class.forName(s.substring(0, idx));
					Enum<?> en = (Enum<?>) enumClass.getMethod("valueOf",
							String.class).invoke(null,
							s.substring(idx + 1, s.length()));
					return en;
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			} else if (Identifier.class.isAssignableFrom(tgt)) {
				return identifier(s);
			} else if (Identifier[].class.isAssignableFrom(tgt)) {
				if ("".equals(s)) {
					return new Identifier[0];
				} else {
					String[] array = s.split(",");
					Identifier[] results = new Identifier[array.length];
					for (int i = 0; i < array.length; i++) {
						results[i] = identifier(array[i]);
					}
					return results;
				}
			} else if (String[].class.isAssignableFrom(tgt)) {
				if ("".equals(s)) {
					return new String[0];
				} else {
					String[] array = s.split(",");
					return array;
				}
			} else {
				return s;
			}
		} else if (RTICLibrary.DDS_TK_LONGLONG == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_longlong(dd, longByReference, null,
							member_id))) {
				return null;
			}

			long l = longByReference.getValue();
			// Callers need to know long will be returned where Date was sent
			if (Date.class.equals(tgt)) {
				date.setTime(l);
				return date;
			} else {
				return l;
			}

		} else if (RTICLibrary.DDS_TK_LONG == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_long(dd, intByReference, null,
							member_id))) {
				return null;
			} else {
				return intByReference.getValue();
			}
		} else if (RTICLibrary.DDS_TK_SHORT == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_short(dd, shortByReference, null,
							member_id))) {
				return null;
			} else {
				return shortByReference.getValue();
			}
		} else if (RTICLibrary.DDS_TK_CHAR == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_octet(dd, byteByReference, null,
							member_id))) {
				return null;
			} else {
				return byteByReference.getValue();
			}
		} else if (RTICLibrary.DDS_TK_DOUBLE == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_double(dd, doubleByReference, null,
							member_id))) {
				return null;
			} else {
				return doubleByReference.getValue();
			}
		} else if (RTICLibrary.DDS_TK_FLOAT == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_float(dd, floatByReference, null,
							member_id))) {
				return null;
			} else {
				return floatByReference.getValue();
			}
		} else if (RTICLibrary.DDS_TK_BOOLEAN == kind) {
			if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
					.DDS_DynamicData_get_boolean(dd, byteByReference, null,
							member_id))) {
				return null;
			} else {
				return byteByReference.getValue() != 0;
			}
		} else if (RTICLibrary.DDS_TK_ARRAY == kind) {
			return null;
		} else if (RTICLibrary.DDS_TK_SEQUENCE == kind) {
			if (Number[].class.equals(tgt)) {
				RTICLibrary.DDS_Sequence seq = readDoubleSequence;

				if (0 == RTICLibrary.INSTANCE.DDS_DoubleSeq_ensure_length(
						seq.getPointer(), 0, DOUBLE_SEQUENCE_EXTENT)) {
					throw new RuntimeException("Unable to ensure_length");
				}
				if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
						.DDS_DynamicData_get_double_seq(dd, seq.getPointer(),
								null, member_id))) {
					return new Number[0];
				}

				int length = RTICLibrary.INSTANCE.DDS_DoubleSeq_get_length(seq
						.getPointer());
				Number[] numbers = getNumbersByLength(length);

				for (int i = 0; i < length; i++) {
					numbers[i] = RTICLibrary.INSTANCE.DDS_DoubleSeq_get(
							seq.getPointer(), i);
				}
				return numbers;
			} else if (byte[].class.equals(tgt)) {
				RTICLibrary.DDS_Sequence seq = readOctetSequence;

				if (0 == RTICLibrary.INSTANCE.DDS_OctetSeq_ensure_length(
						seq.getPointer(), 0, OCTET_SEQUENCE_EXTENT)) {
					throw new RuntimeException("Unable to ensure_length");
				}
				if (checkReturnCodeForNoData(RTICLibrary.INSTANCE
						.DDS_DynamicData_get_octet_seq(dd, seq.getPointer(),
								null, member_id))) {
					return new byte[0];
				}

				int length = RTICLibrary.INSTANCE.DDS_OctetSeq_get_length(seq
						.getPointer());
				byte[] octets = getOctetsByLength(length);

				for (int i = 0; i < length; i++) {
					octets[i] = RTICLibrary.INSTANCE.DDS_OctetSeq_get(
							seq.getPointer(), i);
				}
				return octets;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	private final StringBuilder sb = new StringBuilder();

	public final void set(String name, Pointer dd, Object o, int member_id,
			int kind, Pointer tc) {
		if (null == o) {
			return;
		}

		if (RTICLibrary.DDS_TK_STRING == kind) {
			if (o instanceof Enum) {
				o = ((Enum<?>) o).getDeclaringClass().getName() + "."
						+ ((Enum<?>) o).name();
			}
			if (o instanceof Identifier) {
				Field f = ((Identifier) o).getField();
				o = fieldName(f);
			}
			if (o instanceof Identifier[]) {
				Identifier[] array = (Identifier[]) o;
				if (array.length == 0) {
					o = "";
				} else {
					sb.delete(0, sb.length());
					sb.append(fieldName(array[0].getField()));

					for (int i = 1; i < array.length; i++) {
						sb.append(",").append(fieldName(array[i].getField()));
					}
					o = sb.toString();
				}
			}
			if (o instanceof String[]) {
				String[] array = (String[]) o;
				if (array.length == 0) {
					o = "";
				} else {
					sb.delete(0, sb.length());
					sb.append(array[0]);

					for (int i = 1; i < array.length; i++) {
						sb.append(",").append(array[i]);
					}
					o = sb.toString();
				}
			}
			Pointer strdup = RTICLibrary.INSTANCE.DDS_String_dup((String) o);
			checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicData_set_string(dd,
					null, member_id, strdup));
		} else if (RTICLibrary.DDS_TK_LONGLONG == kind) {
			if (o instanceof Date) {
				checkReturnCode(RTICLibrary.INSTANCE
						.DDS_DynamicData_set_longlong(dd, null, member_id,
								((Date) o).getTime()));
			} else {
				checkReturnCode(RTICLibrary.INSTANCE
						.DDS_DynamicData_set_longlong(dd, null, member_id,
								(Long) o));
			}
		} else if (RTICLibrary.DDS_TK_LONG == kind) {
			checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicData_set_long(dd,
					null, member_id, (Integer) o));
		} else if (RTICLibrary.DDS_TK_SHORT == kind) {
			checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicData_set_short(dd,
					null, member_id, (Short) o));
		} else if (RTICLibrary.DDS_TK_CHAR == kind) {
			checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicData_set_octet(dd,
					null, member_id, (Byte) o));
		} else if (RTICLibrary.DDS_TK_DOUBLE == kind) {
			if (o instanceof Double) {
				checkReturnCode(RTICLibrary.INSTANCE
						.DDS_DynamicData_set_double(dd, null, member_id,
								(Double) o));
			} else if (o instanceof Number) {
				checkReturnCode(RTICLibrary.INSTANCE
						.DDS_DynamicData_set_double(dd, null, member_id,
								((Number) o).doubleValue()));
			}
		} else if (RTICLibrary.DDS_TK_FLOAT == kind) {
			checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicData_set_float(dd,
					null, member_id, (Float) o));
		} else if (RTICLibrary.DDS_TK_BOOLEAN == kind) {
			checkReturnCode(RTICLibrary.INSTANCE.DDS_DynamicData_set_boolean(
					dd, null, member_id, (Boolean) o));
		} else if (RTICLibrary.DDS_TK_ARRAY == kind) {
			Pointer tc_ = RTICLibrary.INSTANCE.DDS_TypeCode_content_type(tc,
					writeException);
			checkWriteException();
			if (null == tc_) {
				throw new RuntimeException("Unable to get content_type");
			}

//			int componentKind = RTICLibrary.INSTANCE.DDS_TypeCode_kind(tc_,
//					writeException);
//			checkWriteException();

		} else if (RTICLibrary.DDS_TK_SEQUENCE == kind) {
			Pointer tc_ = RTICLibrary.INSTANCE.DDS_TypeCode_content_type(tc,
					writeException);
			checkWriteException();
			if (null == tc_) {
				throw new RuntimeException("Unable to get content_type");
			}
			int componentKind = RTICLibrary.INSTANCE.DDS_TypeCode_kind(tc_,
					writeException);
			checkWriteException();

			if (RTICLibrary.DDS_TK_DOUBLE == componentKind) {
				RTICLibrary.DDS_Sequence seq = readDoubleSequence;
				arrayToSequence((Number[]) o, seq);
				checkReturnCode(RTICLibrary.INSTANCE
						.DDS_DynamicData_set_double_seq(dd, null, member_id,
								seq.getPointer()));
			} else if (RTICLibrary.DDS_TK_OCTET == componentKind) {
				byte[] b = (byte[]) o;
				if (b.length > OCTET_SEQUENCE_EXTENT) {
					throw new RuntimeException("Cannot specify " + b.length
							+ " bytes in this octet sequence member " + name
							+ " because the max_extent is "
							+ OCTET_SEQUENCE_EXTENT);
				}
				RTICLibrary.DDS_Sequence seq = writeOctetSequence;
				arrayToSequence(b, seq);
				log.trace("Converting " + b.length
						+ " bytes to octet_sequence for " + name);
				checkReturnCode(RTICLibrary.INSTANCE
						.DDS_DynamicData_set_octet_seq(dd, null, member_id,
								seq.getPointer()));
			}
		}
	}

	private final RTICLibrary.DDS_Sequence readOctetSequence = new RTICLibrary.DDS_Sequence();
	private final RTICLibrary.DDS_Sequence writeOctetSequence = new RTICLibrary.DDS_Sequence();

	private final RTICLibrary.DDS_Sequence readDoubleSequence = new RTICLibrary.DDS_Sequence();
	private final RTICLibrary.DDS_Sequence writeDoubleSequence = new RTICLibrary.DDS_Sequence();

	private final IntByReference strlen = new IntByReference(STRING_EXTENT);

	private final Memory stringMemory = new Memory(STRING_EXTENT);

	public final Pointer primitive(Class<?> cls) {
		Pointer p = _primitive(cls);
		if (null == p) {
			throw new RuntimeException("Unable to get typecode for " + cls);
		} else {
			return p;
		}
	}

	private final Pointer _primitive(Class<?> cls) {
		Pointer factory = RTICLibrary.INSTANCE
				.DDS_TypeCodeFactory_get_instance();
		if (null == factory) {
			throw new RuntimeException("Unable to get typecode factory");
		}
		if (String.class.equals(cls)) {
			// TODO HORRIBLE .. ASSUMING NO STRINGS LONGER THAN 4096
			// RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(factory,
			// RTICLibrary.DDS_TK_STRING);
			Pointer p = RTICLibrary.INSTANCE
					.DDS_TypeCodeFactory_create_string_tc(factory, 4096,
							writeException);
			checkWriteException();
			return p;
		} else if (Long.class.equals(cls) || Date.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_LONGLONG);
		} else if (Integer.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_LONG);
		} else if (int.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_LONG);
		} else if (Short.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_SHORT);
		} else if (Byte.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_CHAR);
		} else if (Double.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_DOUBLE);
		} else if (Float.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_FLOAT);
		} else if (Boolean.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_BOOLEAN);
		} else if (Enum.class.isAssignableFrom(cls)) {
			Pointer p = RTICLibrary.INSTANCE
					.DDS_TypeCodeFactory_create_string_tc(factory, 1024,
							writeException);
			checkWriteException();
			return p;
		} else if (byte.class.equals(cls)) {
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_OCTET);
		} else if (Number.class.isAssignableFrom(cls)) {
			// Un anticipated number type transferred as double
			return RTICLibrary.INSTANCE.DDS_TypeCodeFactory_get_primitive_tc(
					factory, RTICLibrary.DDS_TK_DOUBLE);
		} else if (cls.isArray()) {
			if (Number.class.equals(cls.getComponentType())) {
				Pointer p = RTICLibrary.INSTANCE
						.DDS_TypeCodeFactory_create_sequence_tc(factory,
								DOUBLE_SEQUENCE_EXTENT,
								primitive(cls.getComponentType()),
								writeException);
				checkWriteException();
				return p;
			} else if (byte.class.equals(cls.getComponentType())) {
				Pointer p = RTICLibrary.INSTANCE
						.DDS_TypeCodeFactory_create_sequence_tc(factory,
								OCTET_SEQUENCE_EXTENT,
								primitive(cls.getComponentType()),
								writeException);
				checkWriteException();
				return p;
			} else if (String.class.equals(cls.getComponentType())) {
				Pointer p = RTICLibrary.INSTANCE
						.DDS_TypeCodeFactory_create_string_tc(factory,
								STRING_EXTENT, writeException);
				checkWriteException();
				return p;
			} else if (Identifier.class.equals(cls.getComponentType())) {
				Pointer p = RTICLibrary.INSTANCE
						.DDS_TypeCodeFactory_create_string_tc(factory,
								STRING_EXTENT, writeException);
				checkWriteException();
				return p;
			} else {
				return null;
			}
		} else if (Identifier.class.isAssignableFrom(cls)) {
			Pointer p = RTICLibrary.INSTANCE
					.DDS_TypeCodeFactory_create_string_tc(factory,
							STRING_EXTENT, writeException);
			checkWriteException();
			return p;
		} else {
			return null;
		}
	}

	public Pointer buildTypeCode(Class<?> iface) {
		return buildTypeCode(iface, null);
	}

	public Pointer buildTypeCode(Class<?> iface, GetterPopulation getters) {

		if (null == getters) {
			getters = populateGetters(iface);
		}

		Pointer factory = RTICLibrary.INSTANCE
				.DDS_TypeCodeFactory_get_instance();
		if (null == factory) {
			throw new RuntimeException("Unable to get factory");
		}

		Pointer tc_comp;

		if (getters.getSparse()) {
			tc_comp = RTICLibrary.INSTANCE
					.DDS_TypeCodeFactory_create_sparse_tc(
							factory,
							RTICLibrary.INSTANCE.DDS_String_dup(iface.getName()),
							0, Pointer.NULL, writeException);
		} else {
			DDS_Sequence sequence = new DDS_Sequence();
			RTICLibrary.INSTANCE
					.DDS_StringSeq_initialize(sequence.getPointer());
			sequence.read();
			tc_comp = RTICLibrary.INSTANCE
					.DDS_TypeCodeFactory_create_struct_tc(
							factory,
							RTICLibrary.INSTANCE.DDS_String_dup(iface.getName()),
							sequence.getPointer(), writeException);
		}

		checkWriteException();
		if (null == tc_comp) {
			throw new RuntimeException("Unable to create typeCode");
		}

		int i = 1;
		for (Getter get : getters.getters) {
			Pointer tc = get.getTypeCode();

			if (tc != null) {

				if (getters.getSparse()) {
					get.setIndex(RTICLibrary.INSTANCE.DDS_TypeCode_add_member(
							tc_comp,
							RTICLibrary.INSTANCE.DDS_String_dup(get.getName()),
							i++, tc, RTICLibrary.DDS_TYPECODE_NONKEY_MEMBER,
							writeException));
					checkWriteException();
					get.setMemberId(i - 1);
					get.setKind(RTICLibrary.INSTANCE.DDS_TypeCode_kind(tc,
							writeException));
					checkWriteException();

					// log.trace(get.getName() + " idx="+get.getIndex() +
					// " idx="+(i-1) + " iface="+iface);

				} else {
					get.setIndex(RTICLibrary.INSTANCE.DDS_TypeCode_add_member(
							tc_comp, RTICLibrary.INSTANCE.DDS_String_dup(get
									.getName()), i++, tc,
							get.isKey() ? RTICLibrary.DDS_TYPECODE_KEY_MEMBER
									: RTICLibrary.DDS_TYPECODE_NONKEY_MEMBER,
							writeException));
					checkWriteException();
					get.setMemberId(i - 1);
					get.setKind(RTICLibrary.INSTANCE.DDS_TypeCode_kind(tc,
							writeException));
					checkWriteException();
				}

				// int idx = sparseTC.add_member(get.getName(), i++, tc,
				// TypeCode.NONKEY_MEMBER, PUBLIC_MEMBER.VALUE, false,
				// TypeCode.NOT_BITFIELD);

			}
		}
		return tc_comp;
	}

	public void set(IdentifiableUpdate<?> update, Pointer dds_update) {
		checkReturnCode(RTICLibrary.INSTANCE
				.DDS_DynamicData_clear_all_members(dds_update));

		for (DDSInterface.Getter m : populateGetters(update.getClass())
				.getGetters()) {
			Object o = m.get(update);
			if (null != o) {
				// +" index:"+m.getIndex());
				set(m.getName(), dds_update, o, m.getMemberId(), m.getKind(),
						m.getTypeCode());
			}
		}
	}

	protected interface Getter {
		Object get(Object o, Object... args);

		String getName();

		Pointer getTypeCode();

		Class<?> getReturnType();

		boolean isKey();

		void setIndex(int index);

		int getIndex();

		void setMemberId(int member_id);

		int getMemberId();

		void setKind(int kind);

		int getKind();

		Method getSetter();

		void setSetter(Method m);
	}

	protected class GetterImpl implements Getter {
		private final Method method;
		private final Pointer typeCode;
		private final boolean key;
		private int index;
		private int member_id;
		private int kind;
		private Method setter;

		@Override
		public Method getSetter() {
			return setter;
		}

		@Override
		public void setSetter(Method m) {
			this.setter = m;
		}

		@Override
		public int getKind() {
			return kind;
		}

		@Override
		public void setKind(int kind) {
			this.kind = kind;
		}

		@Override
		public int getMemberId() {
			return member_id;
		}

		@Override
		public void setMemberId(int member_id) {
			this.member_id = member_id;
		}

		@Override
		public int getIndex() {
			return index;
		}

		@Override
		public void setIndex(int index) {
			this.index = index;
		}

		@Override
		public boolean isKey() {
			return key;
		}

		public GetterImpl(Method method, Persistent p) {
			this.method = method;
			this.typeCode = primitive(method.getReturnType());
			this.key = p.key();

		}

		@Override
		public Object get(Object o, Object... args) {
			try {
				return method.invoke(o, args);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public String getName() {
			return method.getName();
		}

		@Override
		public Pointer getTypeCode() {
			return typeCode;
		}

		@Override
		public Class<?> getReturnType() {
			return method.getReturnType();
		}
	}

	private Map<Class<?>, GetterPopulation> map = new java.util.concurrent.ConcurrentHashMap<Class<?>, GetterPopulation>();

	public static class GetterPopulation {
		private final List<Getter> getters;
		private boolean sparse;

		public GetterPopulation() {
			this.getters = new ArrayList<Getter>();
		}

		public GetterPopulation(List<Getter> getters, boolean sparse) {
			this.getters = getters;
			this.sparse = sparse;
		}

		public List<Getter> getGetters() {
			return getters;
		}

		public boolean getSparse() {
			return sparse;
		}

		public void setSparse(boolean sparse) {
			this.sparse = sparse;
		}
	}

	public GetterPopulation populateGetters(Class<?> iface) {

		GetterPopulation get = map.get(iface);

		if (null != get) {
			return get;
		}

		PersistentClass piface = findPersistent(iface);
		get = map.get(piface.getClazz());

		if (null != get) {
			return get;
		}
		// piface.getClazz());
		get = new GetterPopulation();
		get.setSparse(piface.getPersistent().sparse());

		for (Method m : piface.getClazz().getMethods()) {
			// if(null != m.getDeclaringClass().getAnnotation(Persistent.class))
			// {
			Persistent p = m.getAnnotation(Persistent.class);
			if (null != p) {
				if (m.getName().startsWith("get")
						&& m.getParameterTypes().length == 0) {
					get.getGetters().add(new GetterImpl(m, p));
				} else if (m.getName().startsWith("is")
						&& m.getParameterTypes().length == 0
						&& m.getReturnType().equals(Boolean.class)) {
					get.getGetters().add(new GetterImpl(m, p));
				}

			}
			// }
		}
		Collections.sort(get.getters, new Comparator<Getter>() {
			@Override
			public int compare(Getter o1, Getter o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});

		map.put(iface, get);
		map.put(piface.getClazz(), get);

		return get;
	}

	public static class PersistentClass {
		private final Class<?> clazz;
		private final Persistent persistent;

		public PersistentClass(Class<?> clazz, Persistent persistent) {
			this.clazz = clazz;
			this.persistent = persistent;
		}

		public Class<?> getClazz() {
			return clazz;
		}

		public Persistent getPersistent() {
			return persistent;
		}
	}

	public static final PersistentClass findPersistent(Class<?> cls) {
		if (null == cls) {
			return null;
		}
		PersistentClass pc;
		Persistent _p;
		if (null != (_p = cls.getAnnotation(Persistent.class))) {
			return new PersistentClass(cls, _p);
		}
		for (Class<?> i : cls.getInterfaces()) {
			pc = findPersistent(i);
			if (null != pc) {
				return pc;
			}
		}
		pc = findPersistent(cls.getSuperclass());
		if (null != pc) {
			return pc;
		}

		return null;
	}

	private final Map<Field, String> fieldName = new HashMap<Field, String>();
	private final Map<String, Identifier> identifier = new HashMap<String, Identifier>();

	public final String fieldName(Field f) {
		String s = null;
		if (fieldName.containsKey(f)) {
			s = fieldName.get(f);
		} else {
			s = f.getDeclaringClass().getName() + "." + f.getName();
			fieldName.put(f, s);
		}
		return s;
	}

	public final Identifier identifier(String fieldName) {
		if (null == fieldName || "".equals(fieldName)) {
			return null;
		}
		Identifier i = null;
		if (identifier.containsKey(fieldName)) {
			i = identifier.get(fieldName);
		} else {
			int idx = fieldName.lastIndexOf('.');
			if (idx < 0) {
				throw new IllegalArgumentException("Not a valid fieldName:"
						+ fieldName);
			}
			try {
				i = (Identifier) Class
						.forName(fieldName.substring(0, idx))
						.getField(
								fieldName.substring(idx + 1, fieldName.length()))
						.get(null);
				identifier.put(fieldName, i);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		return i;
	}
}
