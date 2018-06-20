package codecheck;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class App {

	public static void main(String[] args) {
		//		for (int i = 0, l = args.length; i < l; i++) {
		//		String output = String.format("argv[%s]: %s", i, args[i]);
		//		System.out.println(output);
		//	}
		String urlStr = "";
		try {
			urlStr = URLEncoder.encode(args[0], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		Map<String, String> resultmap = convert(callGet("http://challenge-server.code-check.io/api/hash?q=" + urlStr));
		System.out.println(resultmap.get("hash"));

	}

	private static Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("xxxxxxxx.co.jp", 8099));
	private static String proxySwitch = "0";

	public static String callGet(String strGetUrl) {

		HttpURLConnection con = null;
		StringBuffer result = new StringBuffer();

		try {

			URL url = new URL(strGetUrl);

			if (proxySwitch.equals("1")) {
				con = (HttpURLConnection) url.openConnection(proxy);
			} else {
				con = (HttpURLConnection) url.openConnection();
			}

			con.setRequestMethod("GET");
			con.connect();

			// HTTPレスポンスコード
			final int status = con.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				// 通信に成功した
				// テキストを取得する
				final InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				if (null == encoding) {
					encoding = "UTF-8";
				}
				final InputStreamReader inReader = new InputStreamReader(in, encoding);
				final BufferedReader bufReader = new BufferedReader(inReader);
				String line = null;
				// 1行ずつテキストを読み込む
				while ((line = bufReader.readLine()) != null) {
					result.append(line);
				}
				bufReader.close();
				inReader.close();
				in.close();
			} else {
				System.out.println(status);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (con != null) {
				// コネクションを切断
				con.disconnect();
			}
		}
		//      System.out.println("result=" + result.toString());

		return result.toString();

	}

	public static String callPost(String strPostUrl, String strContentType, String formParam) {

		HttpURLConnection con = null;
		StringBuffer result = new StringBuffer();

		try {

			URL url = new URL(strPostUrl);

			if (proxySwitch.equals("1")) {
				con = (HttpURLConnection) url.openConnection(proxy);
			} else {
				con = (HttpURLConnection) url.openConnection();
			}

			con.setDoOutput(true);
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", strContentType);
			OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
			out.write(formParam);
			out.close();
			con.connect();

			// HTTPレスポンスコード
			final int status = con.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				// 通信に成功した
				// テキストを取得する
				final InputStream in = con.getInputStream();
				String encoding = con.getContentEncoding();
				if (null == encoding) {
					encoding = "UTF-8";
				}
				final InputStreamReader inReader = new InputStreamReader(in, encoding);
				final BufferedReader bufReader = new BufferedReader(inReader);
				String line = null;
				// 1行ずつテキストを読み込む
				while ((line = bufReader.readLine()) != null) {
					result.append(line);
				}
				bufReader.close();
				inReader.close();
				in.close();
			} else {
				System.out.println(status);
			}

		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (con != null) {
				// コネクションを切断
				con.disconnect();
			}
		}
		System.out.println("result=" + result.toString());

		return result.toString();

	}

	public static Map<String, String> convert(String script) {
		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("nashorn");

		try {
			// JavaScriptの実行
			Object obj = engine.eval(String.format("(%s)", script));
			// リフレクションでScriptObjectMirrorクラスの取得
			Class scriptClass = Class.forName("jdk.nashorn.api.scripting.ScriptObjectMirror");
			// リフレクションでキーセットを取得
			Object[] keys = ((java.util.Set) obj.getClass().getMethod("keySet").invoke(obj)).toArray();
			// リフレクションでgetメソッドを取得
			Method method_get = obj.getClass().getMethod("get", Class.forName("java.lang.Object"));

			Map<String, String> map = new HashMap<>();
			for (Object key : keys) {
				Object val = method_get.invoke(obj, key);
				map.put(key.toString(), val.toString());
			}

			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}
}
