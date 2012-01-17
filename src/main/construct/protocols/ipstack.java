/**
 * TCP/IP Protocol Stack
*/
package construct.protocols;

import static construct.Core.*;
import static construct.Macros.*;
import static construct.Adapters.*;
import static construct.lib.Containers.*;
import static construct.lib.Binary.*;

import java.net.InetAddress;
import java.net.UnknownHostException;

import construct.Core.KeyFunc;
import construct.lib.Containers.Container;

import static construct.protocols.layer2.ethernet.*;
import static construct.protocols.layer3.ipv4.*;
import static construct.protocols.layer3.ipv6.*;
import static construct.protocols.layer4.udp.*;
import static construct.protocols.layer4.tcp.*;

public class ipstack {

	static Construct layer4_tcp = Struct(
			"layer4_tcp",
	    Rename("header", tcp_header),
	    HexDumpAdapter(
	        Field( "next", new LengthFunc(){
	        	public int length(Container ctx){
	        		Container tcpheader = ctx.get("_");
	        		Integer payload_length = tcpheader.get("payload_length");
	        		Integer header_length = tcpheader.get("header_length");
	       	 		return payload_length - header_length;
	       	 	}}
	        )
	    )
	);

	static Construct layer4_udp = Struct(
			"layer4_udp",
	    Rename("header", udp_header),
	    HexDumpAdapter(
	        Field( "next", new LengthFunc(){
	        	public int length(Container ctx){
	        		Container header = ctx.get("header");
	        		Integer payload_length = header.get("payload_length");
	       	 		return payload_length;
	       	 	}}
	        )
	    )
	);

	static Construct layer3_payload = Switch(
				"next", 
				new KeyFunc(){ public Object key(Container context){
					return context.get("protocol");
				}}, 
				Container( "TCP", layer4_tcp,
									 "UDP", layer4_udp ),
			  Pass,
			  false
   );
	
	static Construct layer3_ipv4 = Struct("layer3_ipv4",
	    Rename("header", ipv4_header),
	    layer3_payload
	);

	static Construct layer3_ipv6 = Struct("layer3_ipv6",
	    Rename("header", ipv6_header),
	    layer3_payload
	);

	static Construct layer2_ethernet = Struct("layer2_ethernet",
	    Rename("header", ethernet_header),
	    Switch(
					"next", 
					new KeyFunc(){ public Object key(Container context){
						return context.get("type");
					}}, 
					Container( "IPv4", layer3_ipv4,
										 "IPv6", layer3_ipv6 ),
				  Pass,
				  false
	   )
	);
	
	static Construct ip_stack = Rename("ip_stack", layer2_ethernet);

  public static void main(String[] args) {
  //Container({'header': Container({
  //'source': '00-11-50-88-6b-57', 
  //'destination': '00-11-50-8c-28-3c', 
  //'type': 'IPv4'}), 'next': Container({
//  	'header': Container({
//  	'header_length': 20, 
//  	'protocol': 'TCP', 
//  	'payload_length': 469, 
//  	'tos': Container({
//  	'minimize_cost': False, 'high_throuput': False, 'minimize_delay': False, 'precedence': 0, 'high_reliability': False}), 
//  	'frame_offset': 0, 'flags': Container({
//  	'dont_fragment': True, 'more_fragments': False}), 
//  	'source': '192.168.2.2', 'destination': '82.94.237.218', 'version': 4, 'identification': 28999, 
//  	'ttl': 128, 'total_length': 489, 'checksum': 34020, 'options': ''}), 'next': Container({'header': 
//  	Container({'header_length': 20, 'seq': 3650012701L, 'urgent': 0, 'ack': 1425971069, 'checksum': 24012, 'destination': 80, 'source': 4394, 'window': 17520, 'flags': Container({'ece': False, 'urg': False, 'ack': True, 'cwr': False, 'psh': True, 'syn': False, 'rst': False, 'ns': False, 'fin': False}), 'options': ''}), 'next': 'GET / HTTP/1.1\r\nHost: www.python.org\r\nUser-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.1) Gecko/20060111 Firefox/1.5.0.1\r\nAccept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\nAccept-Language: en-us,en;q=0.5\r\nAccept-Encoding: gzip,deflate\r\nAccept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\nKeep-Alive: 300\r\nConnection: keep-alive\r\nPragma: no-cache\r\nCache-Control: no-cache\r\n\r\n'})})})
  //'\x00\x11P\x8c(<\x00\x11P\x88kW\x08\x00E\x00\x01\xe9qG@\x00\x80\x06\x84\xe4\xc0\xa8\x02\x02R^\xed\xda\x11*\x00P\xd9\x8e\xc6\x1dT\xfe\x97}P\x18Dp]\xcc\x00\x00GET / HTTP/1.1\r\nHost: www.python.org\r\nUser-Agent: Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.8.0.1) Gecko/20060111 Firefox/1.5.0.1\r\nAccept: text/xml,application/xml,application/xhtml+xml,text/html;q=0.9,text/plain;q=0.8,image/png,*/*;q=0.5\r\nAccept-Language: en-us,en;q=0.5\r\nAccept-Encoding: gzip,deflate\r\nAccept-Charset: ISO-8859-1,utf-8;q=0.7,*;q=0.7\r\nKeep-Alive: 300\r\nConnection: keep-alive\r\nPragma: no-cache\r\nCache-Control: no-cache\r\n\r\n'
  //--------------------------------------------------------------------------------
  //Container({'header': Container({'source': '00-11-50-f2-c2-80', 'destination': '00-02-e3-42-60-09', 'type': 'IPv4'}), 'next': Container({'header': Container({'header_length': 20, 'protocol': 'TCP', 'payload_length': 1412, 'tos': Container({'minimize_cost': False, 'high_throuput': False, 'minimize_delay': True, 'precedence': 4, 'high_reliability': False}), 'frame_offset': 0, 'flags': Container({'dont_fragment': False, 'more_fragments': False}), 'source': '209.73.186.238', 'destination': '192.168.2.60', 'version': 4, 'identification': 64802, 'ttl': 54, 'total_length': 1432, 'checksum': 12945, 'options': ''}), 'next': Container({'header': Container({'header_length': 20, 'seq': 998942684, 'urgent': 0, 'ack': 3303376902L, 'checksum': 52941, 'destination': 3267, 'source': 80, 'window': 65535, 'flags': Container({'ece': False, 'urg': False, 'ack': True, 'cwr': False, 'psh': False, 'syn': False, 'rst': False, 'ns': False, 'fin': False}), 'options': ''}), 'next': 'HTTP/1.1 200 OK\r\nDate: Fri, 15 Dec 2006 21:26:25 GMT\r\nP3P: policyref="http://p3p.yahoo.com/w3c/p3p.xml", CP="CAO DSP COR CUR ADM DEV TAI PSA PSD IVAi IVDi CONi TELo OTPi OUR DELi SAMi OTRi UNRi PUBi IND PHY ONL UNI PUR FIN COM NAV INT DEM CNT STA POL HEA PRE GOV"\r\nCache-Control: private\r\nVary: User-Agent\r\nSet-Cookie: D=_ylh=X3oDMTFkdGloZzVxBF9TAzI3MTYxNDkEcGlkAzExNjYyMTc1NTcEdGVzdAMwBHRtcGwDaW5kZXgtbA--; path=/; domain=.yahoo.com\r\nConnection: close\r\nTransfer-Encoding: chunked\r\nContent-Type: text/html; charset=utf-8\r\nContent-Encoding: gzip\r\n\r\n6bc8   \r\n\x1f\x8b\x08\x00\x00\x00\x00\x00\x00\x03\xdc\xbdiw\xdb8\xb2\x00\xfa\xf9\xfa\x9c\xf9\x0f\x882m\xd9\xb1\x16\x92\x12\xb5\xd8\x91s\x9c\xd8N\xd2\x93m\x12w\xa7\xd3\xcb\xf1\xa1HJbL\x91\x0cIy\x89;\xbf\xec}{\xbf\xecUa!\x01.\xb2\x9de\xe6\xbe{\xe7v,\x92@\xa1P(T\x15\n\x85\xc2\xc3{\x87\xaf\x9f\x9c|xsD\x9e\x9d\xbc|A\xde\xfc\xf2\xf8\xc5\xf3\'\xa4\xd1\xeev\xdf\xf7\x9et\xbb\x87\'\x87\xecC\xbf\xa3\xe9\xdd\xee\xd1\xab\x06i,\xd24\xda\xedv/..:\x17\xbdN\x18\xcf\xbb\'o\xbb\x8bt\xe9\xf7\xbbI\x1a{v\xdaqR\xa7\xb1\xbf\xf1\x10\xdf\xed?\\\xb8\x96\x03\x0fK7\xb5\x08Vm\xbb\x9fV\xde\xf9\xa4\xf1$\x0cR7H\xdb\'W\x91\xdb 6{\x9a4R\xf72\xa5\xd0\xf6\x88\xbd\xb0\xe2\xc4M\'\xbf\x9c\x1c\xb7G\x080\xb1c/JI\n5x\xc1\x8f\xd6\xb9\xc5\xde\xc2\xf7s+&Ax1\t\xdc\x0brh\xa5n+\xd5\'\xa91I{\x93\xb4?I\xcdI:\x98\xa4\xc3I:\x9a\xa4\xe3I\xaak\xf0\x1f|\xd7\x8d\x89\xd6\xb2\xedI\xb3\xd9\xba\xf2#\xf8\xb3\x07\xb5\x00Jg\xee\xa6\'\xde\xd2\xdd\xda\xde\xdbx\xd8em\xe4(\xf8V0_Ysw\x92#\xb0\xff\xf0^\xbb\xbd\xe1\xdbg\x08*I\x92\x89\xae\x0f\x06\x86>\x1c\x8fL\x069\xea\xcc\xbd\xd9\xa3Tz\xbfy\x8a\x1f\x0e\xc6\xc7?\xaf\xd2\xe4\xf1Q\xff\xd5\xf1\xc9\xe2\xf9\xe3\x7f\xf7Nt\xfb\xdd\xd9A\xb3u\xea\xdb\tB\xb3\xe3\xd5r:i\xf6\x06\x03s\xa6\xcf\xf4\x9em\xf5\x86\xda\xc8\xb1\x1cM\x1f\x1a\xfd\x811\x9b\r\xf4^o\xd4\x92Zl\xeem\xb4\xdb\xfb\x19\xe2%\xbc\x1b\x12\xe5j\t\x8a\xed\x93\tq\\;t\xdc_\xde>\x7f\x12.\xa30\x80a\xda\xc2/@\x18\xa4\xf8\x87\x83g\xaf_O.\xbc\xc0\x01\xa2\xd1\x87\xbf\xff\xbe\xfe\xb2G\x7fu\x02k\xe9&\x91e\xbb\x93\xd9*\xb0S/\x0c\xb6\x82d\xfb\xda\x9bm\xdd\x0b\x92\xbf\xff\x86\x7f:\xbe\x1b\xcc\xd3\xc5\xf6u\xec\xa6\xab8 \xc1\xca\xf7\xf7\xbe \xe0Sc\x02\x9f\x93\xc8\xf7\xd2\xadF\xa7\xb1\xbdG_\xf6&\x14\xf2\xde,\x8c\xb7\xf0\x857\xd9:5\xfe\xd0\xfe\x9aL\x1a\xf4Cc\xfb\x91\xbe\xab\xedy\x0fO\r\x0ezog\xc7\xdb\xbe>\xed\xfd\x01\xe5\xbc\xbf\xfe\x9ad\xbf(\x9e\x000{\xb1\xf7\x85#q\xda\xdb\x13=\xf0\xc3y\x8e\xfb\xa9\xd9:\x1d\xb4N\x87\xdb\xd7\xd8\xb4\xcfP\xe9\\x\x0e0K\xe7E8\x9f\xbb\xf1\x1e\xf4\xcd\xdf\xdc\xf4\xb1b\xd6)\xfa\x94\xd7\xde\xfb\xe2\xfa\x89+>\xce,x\xd8\xfb"\x1a\x84Qp\x03Gjs\xdc:\xd55\xd6\xe2,\x7f\xbd\r\xb8\xcf:Q\x1c\xa6!\x1d>(\x93?\xed\x9d\x8e\xa5O8\x1ff\xc0\xc7\xf2\xcb\x0eL8\x98\xad+;\r\xe3\xc9\xe9\x18\xbf%\xab\xc8\x8dm\xdf\x02\xd6U\x81A\x8f\x94\x17J\xdd\xc9\xeb\xe9G\x17\xe6|\xe5W {mE\xf8\x92w:\xe3\x93\xad\xc6*\xf5|\x18\xec\xd2{F\xe5\xaa/\xee\xa5\xb5\x8c|\x17>m\x94\xbe\x1d;\xd5\xaf\xa3\xfc\xf5q\xd4\t\xde\xd9\xb1\xeb\x06\xef=\']\x00\xc3o%\xf4\x91ln\xd2\xa9\x11\xce\xf8\x8b\x0eL\x0e\xcf\xa7\xa5\xb6\'\x93f\x00\xb3\xd2\x8d\x9b\xdb\xe4\x11'})})})
  //'\x00\x02\xe3B`\t\x00\x11P\xf2\xc2\x80\x08\x00E\x90\x05\x98\xfd"\x00\x006\x062\x91\xd1I\xba\xee\xc0\xa8\x02<\x00P\x0c\xc3;\x8a\xa7\xdc\xc4\xe5\x88\x06P\x10\xff\xff\xce\xcd\x00\x00HTTP/1.1 200 OK\r\nDate: Fri, 15 Dec 2006 21:26:25 GMT\r\nP3P: policyref="http://p3p.yahoo.com/w3c/p3p.xml", CP="CAO DSP COR CUR ADM DEV TAI PSA PSD IVAi IVDi CONi TELo OTPi OUR DELi SAMi OTRi UNRi PUBi IND PHY ONL UNI PUR FIN COM NAV INT DEM CNT STA POL HEA PRE GOV"\r\nCache-Control: private\r\nVary: User-Agent\r\nSet-Cookie: D=_ylh=X3oDMTFkdGloZzVxBF9TAzI3MTYxNDkEcGlkAzExNjYyMTc1NTcEdGVzdAMwBHRtcGwDaW5kZXgtbA--; path=/; domain=.yahoo.com\r\nConnection: close\r\nTransfer-Encoding: chunked\r\nContent-Type: text/html; charset=utf-8\r\nContent-Encoding: gzip\r\n\r\n6bc8   \r\n\x1f\x8b\x08\x00\x00\x00\x00\x00\x00\x03\xdc\xbdiw\xdb8\xb2\x00\xfa\xf9\xfa\x9c\xf9\x0f\x882m\xd9\xb1\x16\x92\x12\xb5\xd8\x91s\x9c\xd8N\xd2\x93m\x12w\xa7\xd3\xcb\xf1\xa1HJbL\x91\x0cIy\x89;\xbf\xec}{\xbf\xecUa!\x01.\xb2\x9de\xe6\xbe{\xe7v,\x92@\xa1P(T\x15\n\x85\xc2\xc3{\x87\xaf\x9f\x9c|xsD\x9e\x9d\xbc|A\xde\xfc\xf2\xf8\xc5\xf3\'\xa4\xd1\xeev\xdf\xf7\x9et\xbb\x87\'\x87\xecC\xbf\xa3\xe9\xdd\xee\xd1\xab\x06i,\xd24\xda\xedv/..:\x17\xbdN\x18\xcf\xbb\'o\xbb\x8bt\xe9\xf7\xbbI\x1a{v\xdaqR\xa7\xb1\xbf\xf1\x10\xdf\xed?\\\xb8\x96\x03\x0fK7\xb5\x08Vm\xbb\x9fV\xde\xf9\xa4\xf1$\x0cR7H\xdb\'W\x91\xdb 6{\x9a4R\xf72\xa5\xd0\xf6\x88\xbd\xb0\xe2\xc4M\'\xbf\x9c\x1c\xb7G\x080\xb1c/JI\n5x\xc1\x8f\xd6\xb9\xc5\xde\xc2\xf7s+&Ax1\t\xdc\x0brh\xa5n+\xd5\'\xa91I{\x93\xb4?I\xcdI:\x98\xa4\xc3I:\x9a\xa4\xe3I\xaak\xf0\x1f|\xd7\x8d\x89\xd6\xb2\xedI\xb3\xd9\xba\xf2#\xf8\xb3\x07\xb5\x00Jg\xee\xa6\'\xde\xd2\xdd\xda\xde\xdbx\xd8em\xe4(\xf8V0_Ysw\x92#\xb0\xff\xf0^\xbb\xbd\xe1\xdbg\x08*I\x92\x89\xae\x0f\x06\x86>\x1c\x8fL\x069\xea\xcc\xbd\xd9\xa3Tz\xbfy\x8a\x1f\x0e\xc6\xc7?\xaf\xd2\xe4\xf1Q\xff\xd5\xf1\xc9\xe2\xf9\xe3\x7f\xf7Nt\xfb\xdd\xd9A\xb3u\xea\xdb\tB\xb3\xe3\xd5r:i\xf6\x06\x03s\xa6\xcf\xf4\x9em\xf5\x86\xda\xc8\xb1\x1cM\x1f\x1a\xfd\x811\x9b\r\xf4^o\xd4\x92Zl\xeem\xb4\xdb\xfb\x19\xe2%\xbc\x1b\x12\xe5j\t\x8a\xed\x93\tq\\;t\xdc_\xde>\x7f\x12.\xa30\x80a\xda\xc2/@\x18\xa4\xf8\x87\x83g\xaf_O.\xbc\xc0\x01\xa2\xd1\x87\xbf\xff\xbe\xfe\xb2G\x7fu\x02k\xe9&\x91e\xbb\x93\xd9*\xb0S/\x0c\xb6\x82d\xfb\xda\x9bm\xdd\x0b\x92\xbf\xff\x86\x7f:\xbe\x1b\xcc\xd3\xc5\xf6u\xec\xa6\xab8 \xc1\xca\xf7\xf7\xbe \xe0Sc\x02\x9f\x93\xc8\xf7\xd2\xadF\xa7\xb1\xbdG_\xf6&\x14\xf2\xde,\x8c\xb7\xf0\x857\xd9:5\xfe\xd0\xfe\x9aL\x1a\xf4Cc\xfb\x91\xbe\xab\xedy\x0fO\r\x0ezog\xc7\xdb\xbe>\xed\xfd\x01\xe5\xbc\xbf\xfe\x9ad\xbf(\x9e\x000{\xb1\xf7\x85#q\xda\xdb\x13=\xf0\xc3y\x8e\xfb\xa9\xd9:\x1d\xb4N\x87\xdb\xd7\xd8\xb4\xcfP\xe9\\x\x0e0K\xe7E8\x9f\xbb\xf1\x1e\xf4\xcd\xdf\xdc\xf4\xb1b\xd6)\xfa\x94\xd7\xde\xfb\xe2\xfa\x89+>\xce,x\xd8\xfb"\x1a\x84Qp\x03Gjs\xdc:\xd55\xd6\xe2,\x7f\xbd\r\xb8\xcf:Q\x1c\xa6!\x1d>(\x93?\xed\x9d\x8e\xa5O8\x1ff\xc0\xc7\xf2\xcb\x0eL8\x98\xad+;\r\xe3\xc9\xe9\x18\xbf%\xab\xc8\x8dm\xdf\x02\xd6U\x81A\x8f\x94\x17J\xdd\xc9\xeb\xe9G\x17\xe6|\xe5W {mE\xf8\x92w:\xe3\x93\xad\xc6*\xf5|\x18\xec\xd2{F\xe5\xaa/\xee\xa5\xb5\x8c|\x17>m\x94\xbe\x1d;\xd5\xaf\xa3\xfc\xf5q\xd4\t\xde\xd9\xb1\xeb\x06\xef=\']\x00\xc3o%\xf4\x91ln\xd2\xa9\x11\xce\xf8\x8b\x0eL\x0e\xcf\xa7\xa5\xb6\'\x93f\x00\xb3\xd2\x8d\x9b\xdb\xe4\x11'
  	String cap1 = 
  	    "0011508c283c001150886b570800450001e971474000800684e4c0a80202525eedda11" +
  	    "2a0050d98ec61d54fe977d501844705dcc0000474554202f20485454502f312e310d0a" +
  	    "486f73743a207777772e707974686f6e2e6f72670d0a557365722d4167656e743a204d" +
  	    "6f7a696c6c612f352e30202857696e646f77733b20553b2057696e646f7773204e5420" +
  	    "352e313b20656e2d55533b2072763a312e382e302e3129204765636b6f2f3230303630" +
  	    "3131312046697265666f782f312e352e302e310d0a4163636570743a20746578742f78" +
  	    "6d6c2c6170706c69636174696f6e2f786d6c2c6170706c69636174696f6e2f7868746d" +
  	    "6c2b786d6c2c746578742f68746d6c3b713d302e392c746578742f706c61696e3b713d" +
  	    "302e382c696d6167652f706e672c2a2f2a3b713d302e350d0a4163636570742d4c616e" +
  	    "67756167653a20656e2d75732c656e3b713d302e350d0a4163636570742d456e636f64" +
  	    "696e673a20677a69702c6465666c6174650d0a4163636570742d436861727365743a20" +
  	    "49534f2d383835392d312c7574662d383b713d302e372c2a3b713d302e370d0a4b6565" +
  	    "702d416c6976653a203330300d0a436f6e6e656374696f6e3a206b6565702d616c6976" +
  	    "650d0a507261676d613a206e6f2d63616368650d0a43616368652d436f6e74726f6c3a" +
  	    "206e6f2d63616368650d0a0d0a";
  	
  	String cap2 = 
  			"0002e3426009001150f2c280080045900598fd22000036063291d149baeec0a8023c00" +
  	    "500cc33b8aa7dcc4e588065010ffffcecd0000485454502f312e3120323030204f4b0d" +
  	    "0a446174653a204672692c2031352044656320323030362032313a32363a323520474d" +
  	    "540d0a5033503a20706f6c6963797265663d22687474703a2f2f7033702e7961686f6f" +
  	    "2e636f6d2f7733632f7033702e786d6c222c2043503d2243414f2044535020434f5220" +
  	    "4355522041444d20444556205441492050534120505344204956416920495644692043" +
  	    "4f4e692054454c6f204f545069204f55522044454c692053414d69204f54526920554e" +
  	    "5269205055426920494e4420504859204f4e4c20554e49205055522046494e20434f4d" +
  	    "204e415620494e542044454d20434e542053544120504f4c204845412050524520474f" +
  	    "56220d0a43616368652d436f6e74726f6c3a20707269766174650d0a566172793a2055" +
  	    "7365722d4167656e740d0a5365742d436f6f6b69653a20443d5f796c683d58336f444d" +
  	    "54466b64476c6f5a7a567842463954417a49334d5459784e446b4563476c6b417a4578" +
  	    "4e6a59794d5463314e5463456447567a64414d7742485274634777446157356b5a5867" +
  	    "7462412d2d3b20706174683d2f3b20646f6d61696e3d2e7961686f6f2e636f6d0d0a43" +
  	    "6f6e6e656374696f6e3a20636c6f73650d0a5472616e736665722d456e636f64696e67" +
  	    "3a206368756e6b65640d0a436f6e74656e742d547970653a20746578742f68746d6c3b" +
  	    "20636861727365743d7574662d380d0a436f6e74656e742d456e636f64696e673a2067" +
  	    "7a69700d0a0d0a366263382020200d0a1f8b0800000000000003dcbd6977db38b200fa" +
  	    "f9fa9cf90f88326dd9b1169212b5d891739cd84ed2936d1277a7d3cbf1a1484a624c91" +
  	    "0c4979893bbfec7d7bbfec556121012eb29d65e6be7be7762c9240a1502854150a85c2" +
  	    "c37b87af9f9c7c7873449e9dbc7c41defcf2f8c5f327a4d1ee76dff79e74bb872787ec" +
  	    "43bfa3e9ddeed1ab06692cd234daed762f2e2e3a17bd4e18cfbb276fbb8b74e9f7bb49" +
  	    "1a7b76da7152a7b1bff110dfed3f5cb896030f4b37b508566dbb9f56def9a4f1240c52" +
  	    "3748db275791db20367b9a3452f732a5d0f688bdb0e2c44d27bf9c1cb7470830b1632f" +
  	    "4a490a3578c18fd6b9c5dec2f7732b2641783109dc0b7268a56e2bd527a931497b93b4" +
  	    "3f49cd493a98a4c3493a9aa4e349aa6bf01f7cd78d89d6b2ed49b3d9baf223f8b307b5" +
  	    "004a67eea627ded2dddadedb78d8656de428f856305f5973779223b0fff05ebbbde1db" +
  	    "67082a499289ae0f06863e1c8f4c0639eaccbdd9a3547abf798a1f0ec6c73fafd2e4f1" +
  	    "51ffd5f1c9e2f9e37ff74e74fbddd941b375eadb0942b3e3d5723a69f6060373a6cff4" +
  	    "9e6df586dac8b11c4d1f1afd81319b0df45e6fd4925a6cee6db4dbfb19e225bc1b12e5" +
  	    "6a098aed9309715c3b74dc5fde3e7f122ea3308061dac22f4018a4f8878367af5f4f2e" +
  	    "bcc001a2d187bfffbefeb2477f75026be9269165bb93d92ab0532f0cb68264fbda9b6d" +
  	    "dd0b92bfff867f3abe1bccd3c5f675eca6ab3820c1caf7f7be20e05363029f93c8f7d2" +
  	    "ad46a7b1bd475ff62614f2de2c8cb7f08537d93a35fed0fe9a4c1af44363fb91beabed" +
  	    "790f4f0d0e7a6f67c7dbbe3eedfd01e5bcbffe9a64bf289e00307bb1f7852371dadb13" +
  	    "3df0c3798efba9d93a1db44e87dbd7d8b4cf50e95c780e304be745389fbbf11ef4cddf" +
  	    "dcf4b162d629fa94d7defbe2fa892b3ece2c78d8fb221a84517003476a73dc3ad535d6" +
  	    "e22c7fbd0db8cf3a511ca6211d3e28933fed9d8ea54f381f66c0c7f2cb0e4c3898ad2b" +
  	    "3b0de3c9e918bf25abc88d6ddf02d65581418f94174addc9ebe94717e67ce557207b6d" +
  	    "45f892773ae393adc62af57c18ecd27b46e5aa2feea5b58c7c173e6d94be1d3bd5afa3" +
  	    "fcf571d409ded9b1eb06ef3d275d00c36f25f4916c6ed2a911cef88b0e4c0ecfa7a5b6" +
  	    "27936600b3d28d9bdbe411"; 
  	    
  	Container c = ip_stack.parse(hexStringToByteArray(cap1));
  	System.out.println(c);
  	byte[] ba = ip_stack.build(c);
  	System.out.println( byteArrayToHexString(ba) );

  }
  
}
