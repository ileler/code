package coderr.kerwin.osgi.demo.impl2;

import java.util.Random;

import org.springframework.stereotype.Service;

import coderr.kerwin.osgi.demo.api.GeneratePRCode;

@Service("gCode2")
public class GeneratePRCodeImpl2 implements GeneratePRCode {

	private static int min = 1000;
	private static int max = 99999999;

	@Override
	public synchronized String generatePRCode(String id) {
		if (id == null || "".equals(id)) 	return null;
		
		//检查id数据是否存在并检查是否已生成code、如果数据不存在则返回null、如果已生成则返回code
		
		//数据存在且未生成code则获取最后一条有效的code并且加1
		int lastCode = getLastCode() + 1;
		
		//判断最新生成的code是否超过最大限制
		return lastCode > max ? null : "[2]"+String.valueOf(lastCode);
	}
	
	private int getLastCode() {
		return min + new Random().nextInt(max - min);
	}

}
