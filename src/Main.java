import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Scanner;

public class Main {

    static int bitLength=6;
    static SecureRandom sr = new SecureRandom();
    static int n;
    static int k;

    public static void main(String[] args) {
        BigInteger secret = BigInteger.probablePrime(bitLength,sr);
        BigInteger prime;
        do prime = BigInteger.probablePrime(bitLength,sr);
        while (prime.compareTo(secret)<=0);
        System.out.println("The secret is " + secret);
        System.out.println("The prime is " + prime);

        System.out.println("How many shares do you want to have?");
        Scanner s = new Scanner(System.in);
        String pom = s.nextLine();
        n=Integer.parseInt(pom);
        System.out.println("How many shares is needed to recreate secret?");
        pom=s.nextLine();
        k=Integer.parseInt(pom);

        BigInteger [] randoms = new BigInteger[k];
        randoms[0]=secret;
        for(int i=1;i<k;i++)
        {
            randoms[i]=randomA(prime);
        }

        SecretShare [] ss =split(secret,randoms,prime);

        SecretShare [] toViewSecret = new SecretShare[] {ss[0],ss[3],ss[2],ss[1]};
        SecretShare [] toViewSecret1 = new SecretShare[] {ss[0],ss[3],ss[2]};
        SecretShare [] toViewSecret2 = new SecretShare[] {ss[0],ss[3]};
        BigInteger result = reconstruct(toViewSecret,prime);
        BigInteger result1 = reconstruct(toViewSecret1,prime);
        BigInteger result2= reconstruct(toViewSecret2,prime);
        System.out.println("Result with four shares " + result);
        System.out.println("Result with three shares " + result1);
        System.out.println("Result with two shares " + result2);

        if(result.compareTo(secret)==0)
        {
            System.out.println("The shared secret is good");
        }
        else
        {
            System.out.println("The shared secret is not valid");
        }

        if(result1.compareTo(secret)==0)
        {
            System.out.println("The shared secret is good");
        }
        else
        {
            System.out.println("The shared secret is not valid");
        }

        if(result2.compareTo(secret)==0)
        {
            System.out.println("The shared secret is good");
        }
        else
        {
            System.out.println("The shared secret is not valid");
        }



    }

    public static SecretShare [] split (BigInteger secret, BigInteger[] randoms, BigInteger prime)
    {
        SecretShare [] shares = new SecretShare[n];
        for(int x=1;x<=n;x++)
        {
            BigInteger accum = secret;
            for(int exp=1;exp<k;exp++)
            {
                accum=accum.add(randoms[exp].multiply(BigInteger.valueOf(x).pow(exp)));
            }
            shares[x-1]=new SecretShare(x,accum);
           // System.out.println("Share "+ shares[x-1]);
        }
        return shares;
    }



    public static BigInteger randomA(BigInteger prime)
    {
        while (true)
        {
            BigInteger r = new BigInteger(bitLength,sr);
            if(r.compareTo(BigInteger.ZERO)>0 && r.compareTo(prime)<0){return r;}
        }
    }

    public static  BigInteger reconstruct (SecretShare [] shares, BigInteger prime)
    {
        BigInteger accum = BigInteger.ZERO;
        for(int formula=0;formula<shares.length;formula++)
        {
            BigInteger licznik = BigInteger.ONE;
            BigInteger mianownik = BigInteger.ONE;

            for(int count =0; count<shares.length;count++)
            {
                if(formula==count)continue;

                int start = shares[formula].getNumber();
                int next =shares[count].getNumber();

                licznik = licznik.multiply(BigInteger.valueOf(next).negate());
                mianownik = mianownik.multiply(BigInteger.valueOf(start-next));

            }
            BigInteger value = shares[formula].getShare();
            BigInteger pom = value.multiply(licznik).multiply(modInverse(mianownik,prime));
            accum=prime.add(accum).add(pom).mod(prime);


        }
        //System.out.println("the secret is "+ accum);
        return accum;
    }

    public static BigInteger modInverse(BigInteger pom, BigInteger prime)
    {
        pom=pom.mod(prime);
        BigInteger r = (pom.compareTo(BigInteger.ZERO)==-1)?(gcdD(prime,pom.negate())[2]).negate() : gcdD(prime,pom)[2];
        return prime.add(r).mod(prime);
    }

    private static BigInteger[] gcdD(BigInteger a, BigInteger b)
    {
        if (b.compareTo(BigInteger.ZERO) == 0)
            return new BigInteger[] {a, BigInteger.ONE, BigInteger.ZERO};
        else
        {
            BigInteger n = a.divide(b);
            BigInteger c = a.mod(b);
            BigInteger[] r = gcdD(b, c);
            return new BigInteger[] {r[0], r[2], r[1].subtract(r[2].multiply(n))};
        }
    }

}
