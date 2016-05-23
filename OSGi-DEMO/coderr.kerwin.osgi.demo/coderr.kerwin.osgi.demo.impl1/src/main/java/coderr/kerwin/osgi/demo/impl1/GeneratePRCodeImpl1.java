package coderr.kerwin.osgi.demo.impl1;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSession;
import org.springframework.stereotype.Service;

import coderr.kerwin.osgi.demo.api.GeneratePRCode;
import coderr.kerwin.osgi.demo.model.Account;

@Service("gCode1")
public class GeneratePRCodeImpl1 implements GeneratePRCode {
	
	private Integer min;
	private Integer max;
	  
	public Integer getMin() {
		return min;
	}

	public void setMin(Integer min) {
		this.min = min;
	}

	public Integer getMax() {
		return max;
	}

	public void setMax(Integer max) {
		this.max = max;
	}

	@Resource
    private SqlSession sqlSession;

    private final String MAPPER_NAME_SPACE = "coderr.kerwin.osgi.demo.impl1.";

	@Override
	public synchronized String generatePRCode(String id) {
		if (id == null || "".equals(id)) 	return null;
		
		Object obj = null;
		
		//检查id数据是否存在并检查是否已生成code、如果数据不存在则返回null、如果已生成则返回code
		Account account = (obj = sqlSession.selectOne(MAPPER_NAME_SPACE + "getAccountById", id)) == null ? null : (Account) obj;
		if (account == null || account.getStatus() == 1)	return account == null ? null : account.getCode();
		
		//数据存在且未生成code则获取最后一条有效的code并且加1
//		int lastCode = getLastCode() + 1;
		int lastCode = (obj = sqlSession.selectOne(MAPPER_NAME_SPACE + "getLastCode")) == null ? min : ((int)obj + 1);
		
		//判断最新生成的code是否超过最大限制
		if (lastCode > max) 	return null;
		
		//更新code到数据
		sqlSession.update(MAPPER_NAME_SPACE + "updateCodeById", new Account(Integer.valueOf(id), null, String.valueOf(lastCode), 1));
		return "[1]"+String.valueOf(lastCode);
	}
	
//	private int getLastCode() {
//		return min + new Random().nextInt(max - min);
//	}
	
}
