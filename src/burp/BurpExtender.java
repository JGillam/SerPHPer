package burp;

import com.professionallyevil.serphp.SerPHP;

public class BurpExtender implements IBurpExtender{

    private SerPHP extension = new SerPHP();

    @Override
    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        extension.registerExtenderCallbacks(callbacks);
    }
}
