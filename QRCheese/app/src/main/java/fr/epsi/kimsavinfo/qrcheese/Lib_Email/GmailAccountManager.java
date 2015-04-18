package fr.epsi.kimsavinfo.qrcheese.Lib_Email;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

/**
 * Created by kimsavinfo on 18/04/15.
 * <!> Can't get gmail local password whithout publicing the app
 */
public class GmailAccountManager
{
    // gmailPassword = manager.getPassword(account);
    // java.lang.SecurityException: caller uid XXXXX is different than the authenticator's uid
    public static String getAdress(Context _context)
    {
        String gmailAdress = "";

        AccountManager manager = AccountManager.get(_context);
        Account[] accounts = manager.getAccountsByType("com.google");

        for (Account account : accounts) {
            gmailAdress = account.name;
        }

        return gmailAdress;
    }
}
