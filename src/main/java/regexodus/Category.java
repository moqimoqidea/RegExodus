package regexodus;

import regexodus.ds.CharArrayList;
import regexodus.ds.CharCharMap;
import regexodus.ds.IntBitSet;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Unicode categories constructed from tightly compressed string and array literals instead of large resources.
 * Credit for the technique and much of the code goes to gagern, https://gist.github.com/gagern/89db1179766a702c564d
 * Also, the heavy amount of work that went into the Unicode DB for Node.JS (which the pre-processing stage for this
 * depends on) must be commended; that project is https://github.com/mathiasbynens/node-unicode-data
 */
public class Category {
    public final int length;
    private int n;
    private CharArrayList cal;
    final Block[] blocks;
    private Category()
    {
        length = 0;
        cal = new CharArrayList(0);
        blocks = new Block[0];
    }
    private Category(int[] directory, String data)
    {
        n = data.length();
        int j = 0, len = 0;
        cal = new CharArrayList(n);
        for (int i = 0; i < n; ++i) {
            cal.add(j += directory[data.charAt(i) - 32]);
            if((i & 1) == 1) len += 1 + j - cal.getChar(i-1);
        }
        length = len;
        blocks = makeBlocks();
    }

    public char[] contents()
    {
        int k = 0;
        char[] con = new char[length];
        for (int i = 0; i < n - 1; i += 2)
            for (char e = cal.getChar(i); e <= cal.getChar(i+1); ++e)
                con[k++] = e;
        return con;
    }

    private Block[] makeBlocks() {
        int k = 0;
        Block[] bls = new Block[256];
        IntBitSet[] bss = new IntBitSet[256];
        int e, e2, eb, e2b;
        for (int i = 0; i < n - 1; i += 2) {
            e = cal.getChar(i);
            e2 = cal.getChar(i+1);
            eb = e >>> 8;
            e2b = e2 >>> 8;
            if(bss[eb] == null) bss[eb] = new IntBitSet();
            if(eb == e2b)
            {
                bss[eb].set(e & 0xff, e2 & 0xff);
                continue;
            }
            bss[eb++].set(e & 0xff, 255);
            while (eb != e2b) {
                if(bss[eb] == null) bss[eb] = new IntBitSet();
                bss[eb++].set(0, 255);
            }
            if(bss[e2b] == null) bss[e2b] = new IntBitSet();
            bss[e2b].set(0, e2 & 0xff);
        }
        for (int i = 0; i < 256; i++) {
            if(bss[i] == null)
                bls[i] = new Block();
            else
                bls[i] = new Block(bss[i]);
        }
        return bls;
    }

    public boolean contains(char checking) {
        for (int i = 0; i < n - 1; i += 2) {
            if (checking >= cal.getChar(i) && checking <= cal.getChar(i + 1))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Category{" +
                cal +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Category category = (Category) o;

        if (length != category.length) return false;
        if (n != category.n) return false;
        if (cal != null ? !cal.equals(category.cal) : category.cal != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(blocks, category.blocks);

    }

    @Override
    public int hashCode() {
        int result = length;
        result = 31 * result + n;
        result = 31 * result + (cal != null ? cal.hashCode() : 0);
        result = 31 * result + Arrays.hashCode(blocks);
        return result;
    }
    /**
     * All control, format, surrogate, private use, and unassigned characters; Unicode category C.
     */
    public static final Category C=new Category(new int[]{0,1,2,3,4,5,8,7,6,11,9,10,12,13,14,15,16,27,23,24,20,37,44,48,50,19,22,25,31,32,33,34,39,52,54,56,60,90,17,21,28,29,30,36,43,46,55,57,61,65,71,87,95,104,117,18,26,40,42,45,49,51,58,59,63,64,66,68,73,77,79,96,102,107,112,125,127,136,141,149,162,185,187,191,192,199,215,224,251,284,349,366,367,378,398,664,670,715,1166,1813,6839,8451,11173,21041}," <g=. \u0081!'#& \" G ~ @!]!$ C'H#'02!t \\!D!h-D!8!0 I!\" ,A: *40 p *!#!2 & \"\"%!+!#!%'\"## (!X!$ '##!2 & # # #!\" (##!$\"\"(% \"(W*$ + $ 2 & # (!) $ $!\".%!-(& $ *!#!2 & # (!+!#!$'### (!9*# '\"$ %\"# \" #\"#\"$\"-#(\"$ %!\"%\"-:$. $ 3 F\"* $ %(# $$%!)': $ 3 ) (!+ $ %(#(\" %!) #,% * $ A $ '#F!1!# 9\"; + \"!&\"\"#' \" *%)!$)_#JK# \"!# \"!\"%% & $ \" \"!# . $!( \" '!)!%<d 5#Y 5 0 .Ku \"$\"!} %!& \" %!Z %!? %!& \" %!0 ^ %!c!>\"1%S!'!\u0080\"E(. &+3&G). $ #)T!)%)%/!)%E(6$R*= -#-#\"\"L!(+[#1%,\"`!b J!,%)%/!0ae#M\"V'P\"0\"P(6!,'L%x y!'!@!'!* \" \" \" =!B 0 /!' 4!$ + ,$1$8/#!H .\">/?.n#\u007f3,4\u0083!>!8 B 7 7 o$M \"$\"!O(#-;&& & & & & & & & j71 E)v;-#Q S!U$6 T 6$5)7 w \u0084*\u0087/\u0082\"C&z9q'rDB#)%O'R'-%V+<\"f ,#? C&/!)!U2I*'!'!'&& & N*l!)%\u0086)3#8\u0085|!i5&)($1 ( \" # # k0{/Q!N@/!1%A 4 %#( m#s\"'!'!'!$\"& &,#! ");

    /**
     * All private use characters; Unicode category Co.
     */
    public static final Category Co=new Category(new int[]{1792,6399,57344},"\"! ");
    
    /**
     * All unassigned characters; Unicode category Cn.
     */
    public static final Category Cn=new Category(new int[]{0,1,2,3,5,4,8,7,6,11,9,10,13,14,16,12,15,27,23,24,20,37,44,48,19,22,25,30,33,34,39,50,52,54,56,61,90,17,21,28,29,31,32,36,43,46,55,57,60,65,71,87,95,102,104,117,18,26,40,42,45,51,58,59,63,64,66,68,73,77,79,107,112,125,127,136,141,149,178,185,187,191,199,215,224,241,251,284,349,366,378,398,664,670,888,1166,1813,6839,8815,11173,21041},"~!'#& \" F { >!]!% B'G#'+; u C!U,P!?!. H!\" /@9 *4n *!#!2 & \"\"$!+!#!$'\"## (!Y!% '##!2 & # # #!\" (##!%\"\"($ \"(X*% + % 2 & # (!) % %!\"-$!,(& % *!#!2 & # (!+!#!%'### (!8*# '\"% $\"# \" #\"#\"%\",#(\"% $!\"$\",9%- % 3 E\"* % $(# %%$!)'9 % 3 ) (!+ % $(#(\" $!) #/$ * % @ % '#E!1!# 8\": + \"!&\"\"#' \" *$)!%)_#;K# \"!# \"!\"$$ & % \" \"!# - %!( \" '!)!$Id 5#Z 5 . -Kr \"%\"!z $!& \" $![ $!= $!& \" $!. ^ $!c!<\"1$S!'!}\"D(- &+3&F)- % #)T!)$)$. )$D(6%R*J ,#,#\"\"L!(+\\#1$/\"`!b ;!/$)$0!.ae#M\"W'C\".\"C(6!/'L$v w!'!>!'!* \" \" \" J!A . 0!' 4!% + U ,!G -\"<0=-l#|3/4\u0080!<!? A 7 7 m%M \"%\"!O(#,:&& & & & & & & & h71 D)s:,#Q S!V%6 T 6%5)7 t \u0081*\u00840\u007f\"B&x8o'pPA#)$O'R',$W+I\"f /#= B&0!)!V2H*'!'!'&& & N*j!)$\u0083)3#?#\u0082!g5&)(%1 ( \" # # i.y0Q!N>0!1$@ 4 $#( k!\" q\"'!'!'!%\"& &*(! ");

    /**
     * All control characters; Unicode category Cc.
     */
    public static final Category Cc=new Category(new int[]{0,31,32,96}," !#\"");

    /**
     * All format characters; Unicode category Cf.
     */
    public static final Category Cf=new Category(new int[]{0,4,2,50,5,9,23,27,173,193,250,1363,2045,4351,56976},"( +$& ) # - ,!'!#!\"%. *\"!");

    /**
     * All surrogate characters; Unicode category Cs.
     */
    public static final Category Cs=new Category(new int[]{2047,55296},"! ");

    /**
     * All letters; Unicode category L.
     */
    public static final Category L=new Category(new int[]{2,0,3,4,1,6,5,7,8,17,11,10,12,15,22,9,25,21,42,16,30,46,13,19,37,14,18,24,29,35,40,53,26,27,32,33,43,88,20,23,28,31,36,48,49,50,51,52,54,55,56,59,63,65,68,69,85,102,117,34,38,39,41,45,47,62,64,66,71,72,74,75,81,82,83,89,93,94,98,105,107,114,116,130,132,134,138,165,191,268,277,332,362,365,457,470,513,619,1164,2684,6581,8453,11171,20975},"U0'0K!*!&!%. 4 ~&*-#(! !s# $\"\" !'!   ! 7 i v/w 8\"!'>e@&\"52J$ n !3$($* \"!)! <4E,!0B+$&!%1&!+!#!;;(+PF 'd?#!7!(/3-&'\"$\"1 % !#\"#!)!9$  -$*!/&&$\"1 % $ $ $B\" !F )(   1 % $ ##!7!3$;!,'\"$\"1 % $ ##!I$  3!:! &#  \"#$ ! $#$# #*G!?'   . -#!A %$I!&'   . / ##!C! $3$7'   >\"!)!% / 0&%)#G ( !\"%S` $6%S$ !\"$ !\"!'\" %   ! !\"$ \" $+!\"# !.\"C!b' =H#r21!)&&\"#!#$( &,6!:8 !%!\"2 { \"\"% ! \"\"> \"\"B \"\"% ! \"\"9 R \"\"c\\-)X\"&#\u0081\"3 0%f''(, \"-)-)-,  3NJ!&!VE(#\"C !%W*4M<\"#,D&0Q.+Oj!m5:%R<9$*DA=2 *=\"((2\" 2\" \"#$+xUz\"&\"8\"&\"' ! ! ! 4\"O % !#  %#\"\"&&,%  %Z!9!),Y!&!\"/ !##'! ! ! \" +\"\"%#&!?$\u00835 5 t'\"#$68 !%!\"Q(!).+% % % % % % % %h!\u007f$D#%$&X'  k \"%2 l:@P-\u0080\u0084g\u0087)\u0082V_\"y#-*$15)4\"WM(\"Y\"5a+   \" .4N-LT&#! $,A*.@H(5<!)# /*# >;  '1.#!#L !#$\"#\"! !0 \"+( 6&\"&\"&+% % 2 /*q4\u00866.&K\u0085}\"o]%6#%! / , # ! $ $ p[|7T\"?^*Z# u80'0,E#&\"&\"&\" =");

    /**
     * All upper-case letters; Unicode category Lu.
     */
    public static final Category Lu=new Category(new int[]{0,2,3,1,4,9,7,5,6,8,10,11,13,12,25,37,50,16,20,22,34,36,42,46,49,62,65,73,85,102,136,197,263,290,321,723,2203,2685,2890,22377,31054},":.=3!(4 ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! \" ! ! ! ! ! ! ! \" ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !#! ! $#! !#!!\"\"!#!!$#!#! ! !#! \" !#!!! !#$ ) \" \" \" ! ! ! ! ! ! ! \" ! ! ! ! ! ! ! ! \" \" !!! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! )#!#\" !\"! ! ! ! A ! $ % & !!! !#!1!)5 \"!$ ! ! ! ! ! ! ! ! ! ! ! ( \" !#\"08 ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! * ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !#! ! ! ! ! ! \" ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! \"/F/! ( C<D6\"!B ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! * ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! *&%'+&%&%'- ! ! ! %&;\",\",\",$-\"@ ' $!\"!\" $$& ! ! !\"\"\"+#( 9 E70 !!\" ! ! !\"! \" %!! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! % ! ' H ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! 2 ! ! ! ! ! ! ! ! ! ! ! ! ! > ! ! ! ! ! ! $ ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! + ! !#! ! ! ! ' ! \" ! $ ! ! ! ! ! ! ! ! ! !$!$! ! G.?");

    /**
     * All lower-letters; Unicode category Ll.
     */
    public static final Category Ll=new Category(new int[]{0,2,3,1,4,5,7,9,6,8,11,13,42,25,28,10,12,19,20,23,26,33,34,37,40,43,46,47,49,52,54,59,64,65,68,79,97,103,120,136,165,194,275,761,822,1066,2179,2732,2888,20289,30996},"D-? ,3!&! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !#! ! ! ! ! ! ! !#! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! \" ! !!\" ! \" $#% \" $!\" \" ! ! \" !#! \" $ ! \"#\"!& \" \" ! ! ! ! ! ! ! !#! ! ! ! ! ! ! ! !#\" ! $ ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !(\" \"#! % ! ! ! !B!4I ! $ $!1 .6!#$!! ! ! ! ! ! ! ! ! ! ! !$! \" \"#=;! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! / ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! \" ! ! ! ! ! !#! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! <8P,\"!K%N)F9@0!5E ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !)! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !)'%*&'&'%*&'&'+\"&'&'&'$!#& $!!#'\"\"#'&*!!#J $#$ . % % \"#'\"% > O:\" $#! ! ! % !#!%( ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !#) ! % +7! ( R ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! 2 ! ! ! ! ! ! ! ! ! ! ! ! ! G ! ! ! ! ! !!! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! ! !&! ! \" ! ! ! ! % ! \" !!! ! ! ! ! ! ! ! ! ! ( ( ! ! A L,(%*CQ(+$M-H");

    /**
     * All title-case letters; Unicode category Lt.
     */
    public static final Category Lt=new Category(new int[]{0,7,3,9,13,16,39,48,453,7574},"( \" \" & )!#!#!$ % ' ");

    /**
     * All modifier letters; Unicode category Lm.
     */
    public static final Category Lm=new Category(new int[]{0,1,5,4,2,6,8,14,17,470,3,10,11,12,15,22,23,24,29,32,35,36,44,46,62,81,94,96,98,104,108,109,112,115,122,128,134,138,165,175,192,231,242,270,271,329,465,479,566,612,688,690,1237,1251,1755,3040,21521,28439},"R(\",.#& $ D % O I F!K!\" 3 + # M T C P V > Q N\"G8' 45S ' (-W!J H ) 6#% <!:$Y U\"L A 2!B&9 1 @!) 0 E ? /!=*X 7!;");

    /**
     * All other letters (those without casing information); Unicode category Lo.
     */
    public static final Category Lo=new Category(new int[]{2,0,3,4,1,6,5,7,15,8,11,17,9,12,22,21,25,10,16,30,14,19,24,29,46,13,18,20,26,27,31,32,33,34,35,40,42,43,53,54,68,114,23,28,41,49,50,51,52,55,56,63,69,170,36,38,39,44,47,48,59,60,64,66,67,71,74,75,85,88,89,93,98,104,105,106,107,116,117,134,146,177,209,257,267,328,362,365,513,552,619,828,1087,1142,3064,6581,8453,11171,20975},"U!2!s!&\"r!{<&\"8> ,V$ h !0$* \"!+! 73e-!0?./E6)1G; 'aF#!5!),+4&'\"$\"/ % !#\"#!+!4$  ($*!,&&$\"/ % $ $ $?\" !; +)   / % $ ##!5!2$6!-'\"$\"/ % $ ##!>$  2!:! &#  \"#$ ! $#$# #*J!F'   . (#!= %$>!&'   . , ##!@! $2$5'   C\"!+!% , 0&%+#J ) !\"%\\Z $9&]$ !\"$ !\"!'\" %   ! !\"$ \" $1!\"#6\"@!^' BK#mD/!+&&\"#!#$) &-9!Iu \"\"% ! \"\"C \"\"? \"\"% ! \"\"4 R \"\"_W(Iz\"2 0%b'')- \"(+(+(-  2OL!HA P)#\"@ !%T*3N7\"#-E&0Q.1Pq8:%R74$*E=BD *7I\" \"#$|\"~Q0.1% % % % % % % %y!G!&d,! f&!%D g:<G(x\u007fc\u0082+; }HX,t&(*$`!NTU!i!#%   \" .3O(MS&#! $-=*.<K)88#\")*# C6  '/( &#!#M !#$\"#\"! !0$#1)!(&\"&\"&1% %pA3\u00819.&[\u0080w\"jH! , - # ! $ $ lAv5S\"FL*n# ok, Y\"3#&\"&\"&\" B");

    /**
     * All marks; Unicode category M.
     */
    public static final Category M=new Category(new int[]{2,0,1,3,4,5,6,10,11,31,57,12,13,7,8,17,9,14,19,30,49,15,20,24,26,27,28,32,35,39,44,46,48,51,58,120,16,21,23,25,29,33,36,37,41,42,45,50,55,56,59,60,62,65,66,68,71,73,75,81,89,92,97,99,101,102,106,111,119,142,153,199,220,237,264,273,276,464,555,704,721,738,768,947,3071,20273,30165},"rcl&j> ! \" \" !Y'46/!a&#%#\" #J!)8]'R.'!G# .   $> C1 ;P  / &(\"3 *! &#\"# '!(\"9!# *! $%\"# $!)\"$!+ *! -    E\"F%  *! &#\"# 0\"(\")!S$$  #'!L$B&   #.\"+\"3 *! &   #.\"+\"H#Q\" &   #'!(\")\"X!%% ! -2\"T!#&,-_!#% \"+%Z\":! ! !%\"O2 \"&' <'!`27#%   #&$#1( !(#o s 3 3\")\"U)'!? C\"<!d(%(h$B0 :#!41V$@D=., )+*,42f  6%!% # g* $p;t e!^)n%b\"v# 0I\"[\"k!$!%!7$\\\"A/9/1!=-8+N#@,K!W,,!0\"? A!  #\"&\" !M$&\"i- \"u!q5/5m");

    /**
     * All non-spacing marks; Unicode category Mn.
     */
    public static final Category Mn=new Category(new int[]{0,2,1,3,5,4,6,7,9,10,12,13,31,59,8,17,21,26,30,49,58,14,11,15,27,33,35,41,44,56,57,103,120,16,20,23,25,28,29,36,39,43,46,47,48,51,52,62,65,67,68,73,75,81,91,92,97,99,102,108,111,119,142,153,158,199,220,239,266,273,276,464,555,704,721,738,768,947,3071,20273,30165},"l\\f%d<! !\"!\"! S)3B/ Z&#$#\"!#G ,1W)-.) D#!.!!!%<!@5!,= ! $'$ %&6\"2 - $#( 0\"8 #\"4 $\"$\"#!% ,\"% *\"4 $%!\"$ 0\"C$! - # !#( ( *\", O + M % 4!&!!#.\"*\"2 - # ' &\"0\"F\"4\"$#( 0\"? .!! V #&+'Y #$!\"*$T\"E ! ! =+!%!\"&)!:) ?#!$!\"#\"1\"$!/#5 #\"' A i!m!2!2\",\"P\"!&( #)) J!@\": ]!$\") '!b\"# - !&! ! #''(# 3+Q#3 !%! & ;.+\"9##\"!!> !\"% !!-'#\"_!!*!&$ ' %\"a>!%j*$ %6n!^ X,h#[\"p $(9\"U\"e % $ 1\"`\"8/5 H'1)K!3 ### ; R$#\"#\"+ ( L N !!#\"&\"! I\"( c # $ o k7/7g");

    /**
     * All enclosing marks; Unicode category Me.
     */
    public static final Category Me=new Category(new int[]{2,0,1,3,1160,1567,5685,34188},"$\"&!%#  ' ");

    /**
     * All combining spacing marks; Unicode category Mc.
     */
    public static final Category Mc=new Category(new int[]{1,0,2,3,5,7,59,11,4,6,9,49,62,10,44,51,172,8,15,17,19,22,25,26,31,42,43,46,48,54,56,61,64,67,76,89,103,110,111,143,146,225,238,331,347,1818,2307,4919,21523,30708},"N!>!#\"*#\" / &\"% # '!.!&\"A!&\"*!\" = &!\"!% # '!D \" (\"\"\"'!9\",#, &!\"(# \" - . &\")\"\"\"'!: B\"%%4 K @!0 $!%!# 7 '\"#)5 #$#!'\"M!1%\" L##\"$ \"$I &!\"!-!\" *$H!+!)!\"(\" ,!8!$ #!?!#\"\"!( +%* 0!3 (!O Q #!C /2G <!+ $ \"#F # 6!;!\"!E!# )!J \" \" \"!P");

    /**
     * All numbers; Unicode category N.
     */
    public static final Category N=new Category(new int[]{9,5,7,0,2,3,119,6,199,4,8,14,19,23,71,87,135,1,10,12,15,18,21,26,29,31,33,39,40,48,50,59,79,97,104,105,109,110,111,116,121,129,139,155,166,183,189,230,240,269,301,321,344,407,413,631,720,727,778,882,1047,1386,1442,21271,29537},"= H1'#%$^ 0 ( V & !!D & & %!F3G  'B C'*5E A & .,Q . X,[$P \" - R I2L \" M / 0 \" \\#)!\" (>%)Y?@6W8]#Z#7*4$T%J 9\"$+: <+` N S!K ; ( - / U _ O");

    /**
     * All decimal digits; Unicode category Nd.
     */
    public static final Category Nd=new Category(new int[]{9,119,7,39,71,87,135,199,23,48,97,129,167,183,230,279,301,407,413,679,1575,1863,21271,35271},") 4 & ' 2 ! ! ! ! ! ! ! ! ! * ! $ / $ 5 # 0 + , \" - % & \" 7 3 # ' ( % 1 6 .");

    /**
     * All "letter numbers" such as Roman numerals; Unicode category Nl.
     */
    public static final Category Nl=new Category(new int[]{2,3,0,8,9,15,26,34,2672,3711,5870,22800,30380},"* ('!!)\"&#% ,$+");

    /**
     * All other kinds of number character; Unicode category No.
     */
    public static final Category No=new Category(new int[]{0,9,5,2,6,3,7,14,1,4,8,15,18,19,21,29,31,33,40,42,59,79,121,134,139,178,199,218,377,434,481,631,727,1078,1140,1173,1386,1686,2358,22474,30065},"9($ %#F\"<\"6#7$;$,*=!A-B!> E )\"&!:+3 @45.?/D C%8!0&#'1!2'H\"G");

    /**
     * Some whitespace characters; Unicode category Z. This has some notable missing characters, like newline, tab (both
     * horizontal and vertical), and carriage return; you may want {@link #Horizontal} and/or {@link #Vertical}, which
     * don't line up to Unicode categories but do match the correct sets of horizontal and vertical whitespace. There is
     * also the option of {@link #Space} as the fusion of both {@link #Horizontal} and {@link #Vertical}.
     */
    public static final Category Z=new Category(new int[]{0,1,6,10,30,32,48,128,2432,4001,5600},"% ' * (#$!\" & ) ");
    /**
     * Some space separator characters; Unicode category Zs.
     */
    public static final Category Zs=new Category(new int[]{0,10,32,37,48,128,2432,4001,5600},"\" % ( &!# $ ' ");
    /**
     * All line separator characters (well, character; there's only one, and it isn't in ASCII); Unicode category Zs.
     */
    public static final Category Zl=new Category(new int[]{0,8232},"! ");

    /**
     * All paragraph separator characters (well, character; there's only one); Unicode category Zp.
     */
    public static final Category Zp=new Category(new int[]{0,8233},"! ");

    /**
     * All punctuation (but not symbols); Unicode category P.
     */
    public static final Category P=new Category(new int[]{0,1,2,3,5,11,4,9,6,13,12,14,17,21,23,27,28,30,32,33,38,45,72,75,91,404,7,8,10,15,19,31,34,36,41,42,44,46,48,50,52,55,60,63,64,65,79,80,87,98,99,100,103,112,113,116,121,122,125,127,129,141,144,150,152,154,156,158,169,172,173,209,217,234,250,262,270,314,368,381,435,467,613,621,634,703,764,829,1086,20819,29699},"3\"\"$\"#%!&!/\"\" 0 \" A ( & %!& & u ' q$C!H \" # # 5!-!\"!+ #!7#T D)i\"I+2 k!% ] X Y 9 n 8 %!d+\" 4#6 7&$!U$e r;` s!5!N\"6!c\"\"\"4<m!h!\\(\"$f(b#J&K!M:* w.'>\"*\"%?!=!t#1!x)O!2'9-L#3!v#\"!V ^E\"1p\"$'#%, ) R 8 z!l\"S % W$o#P!B\"\" G!F Q*,!Z#[!,!j y!g'.@\")\" $ \"!_\"\"$\"#%!&!/\"\" 0 \" \"(a");

    /**
     * All dash punctuation; Unicode category Pd.
     */
    public static final Category Pd=new Category(new int[]{0,1,5,3,11,20,32,38,45,52,112,170,476,1030,1373,2058,3586,3650,52625},"( . ) 1 - /\"0 # &!\" , % * 2!' $ + ");

    /**
     * All starting/opening "bracket-like" punctuation; Unicode category Ps.
     */
    public static final Category Ps=new Category(new int[]{0,2,4,3,32,51,16,18,26,30,31,33,34,39,40,56,65,81,171,216,405,454,635,1062,1087,1887,2431,3775,52514},". % $ ; ! 9 : \" - / & 6 ! * 8 ! ! ! ! ! ! 1 + ! ! ! ! 4 ! ! ! ! ! ! ! ! ! ! 0 ! , 7 ! ! ! ( 5 ! ! ! ! \" ! ! ! # < 3 ) ! ! ! ! ! ! ! \" ' ! ! 2 % $ \" # ");

    /**
     * All initial/opening "quote-like" punctuation; Unicode category Pi.
     */
    public static final Category Pi=new Category(new int[]{0,3,1,2,4,5,16,26,171,3529,8045},"( * !\"! ' ) # % ! & $ ");

    /**
     * All ending/closing "bracket-like" punctuation; Unicode category Pe.
     */
    public static final Category Pe=new Category(new int[]{0,2,3,4,32,52,1,16,18,30,31,33,34,41,56,65,81,171,218,405,480,635,1062,1087,1887,2474,3774,52511},"- % $ : ! 8 9 . ' 5 ! * 7 ! ! ! ! ! ! 0 + ! ! ! ! 3 ! ! ! ! ! ! ! ! ! ! / ! , 6 ! ! ! 4 ! ! ! ! # ! ! ! \"&; 2 ) ! ! ! ! ! ! ! # ( ! ! 1 % $ \" \" ");

    /**
     * All finalizing/closing "quote-like" punctuation; Unicode category Pf.
     */
    public static final Category Pf=new Category(new int[]{0,4,2,3,5,16,29,187,3529,8030},"' ) ! & ( \" $ # % ! ");

    /**
     * All connector punctuation, such as the underscore; Unicode category Pc.
     */
    public static final Category Pc=new Category(new int[]{0,1,2,20,25,95,240,8160,56799},"% '!# (!$\"& ");

    /**
     * All other punctuation; Unicode category Po.
     */
    public static final Category Po=new Category(new int[]{0,2,1,3,4,5,11,8,9,6,7,14,12,17,21,28,55,75,113,125,10,13,15,23,32,33,37,38,41,42,44,45,48,50,58,60,63,65,69,72,87,91,98,100,103,112,116,121,122,127,129,141,144,150,154,156,158,169,172,173,190,217,234,250,262,270,314,368,381,404,435,467,613,703,773,835,3227,21029,29699},"9!!!# ! !\"&\"$\"/ F ) 6\"' i ( g%= 0 # # ?\".\"!\"+ #\"1#L >5^!0+8 `\"& S O P e c I &\"Y+! 2 1$%\"M%Z h'j\"3!G\"X!!!;%!#b\"]\"R)!%[)W#C$D\"E*, k\"(*('###!$4! !(l#!\"2 T\"%!# #'!\"! #\"&$!(##! !&f!B \\ n\"a!K & N%d#H\"<!! A\"@ J,-\"3#Q\"-\"_ m)# 7 .\"##$!!#'!* !\"U!!!# ! !\"&\"$\"/ : #\"V");
    /**
     * All symbols (but not punctuation); Unicode category S.
     */
    public static final Category S=new Category(new int[]{0,2,1,3,5,6,14,7,11,9,31,13,16,4,10,12,28,32,8,15,17,20,22,23,29,30,33,36,48,62,158,198,21,25,26,27,35,38,40,42,45,52,54,59,63,65,70,77,82,88,92,101,104,113,118,119,127,130,131,133,134,137,140,155,194,207,208,213,226,231,244,246,248,251,255,267,354,357,373,375,402,406,459,499,570,571,574,615,753,1089,1090,6593,20430,22161},"; ' 4!1 ! 0 ! ;-!\"# !## - * 1 r#++%%! !,V 3\"U ^ k!W!# #\"a ( 5\"h 2\"s\"'\"g X Z'[ b G ? `!, !!#$@ ! ! \\'!$!\"%#?\"x)z m >:l).2y !!/!&!&!&\"N & F!&!5*M\"!#!\"( !!%$! ! ! $ /\"$-%#! K\"$o$0#iB.PO7wH<#9(p7=$*#n#*#<!Ie$qA!Q+cC() &\"+ 6\"'\"R\"f\"$):D89/8) ,*(E,=!j{L}Jt6.\"T\">#(#v!d | ]3u\"S !!# _ ' 4!1 ! 0 ! Y%!%&\"!");

    /**
     * All math symbols; Unicode category Sm.
     */
    public static final Category Sm=new Category(new int[]{0,2,3,5,1,4,7,32,62,14,17,31,40,257,6,8,10,11,15,19,20,23,24,30,33,38,41,43,46,49,55,69,91,112,130,132,140,165,267,337,470,528,767,825,6716,53213},"; *!( ! < # 9 ' J I!L ) ,!)!D ,%& ?%.$# \" \" / '$\" ! 'F8$@ +6:#H 0 >&A G%\"712-B5(#+\"-=4\"#M K !!E *!( ! C &\"3");

    /**
     * All currency symbols; Unicode category Sc.
     */
    public static final Category Sc=new Category(new int[]{0,1,499,3,4,8,25,31,36,109,124,126,155,220,246,264,582,1258,2245,2460,21956,34681},"( +#1 * \"!\"!% . / 0 3 2'5 4 ) , -!$!&");

    /**
     * All modifier symbols; Unicode category Sk.
     */
    public static final Category Sk=new Category(new int[]{0,2,1,14,6,13,15,3,4,5,7,10,12,16,22,28,72,94,104,118,163,522,893,977,4253,7224,20567,30308},"1 ! 0 * ) ( 5'%%$$! !-3 &\"9 !!,!#!#!#\"8\";.+\"2\"7 :&6 ! 4 /");

    /**
     * All other symbols; Unicode category So.
     */
    public static final Category So=new Category(new int[]{0,2,1,3,5,7,9,11,6,10,16,29,30,4,8,12,13,14,22,26,31,33,38,255,15,19,20,21,23,25,27,35,39,42,43,45,47,48,52,53,54,59,62,63,65,68,77,80,82,88,110,128,131,133,134,158,166,182,198,207,208,213,231,247,248,267,269,337,354,374,392,406,487,513,516,574,753,866,978,1412,1447,6593,21380,22161},"X # $ ! n a\"S\"[ ' :\"` j e T$! U \\ A f!* !!#$; ! ! V%!$!\"(#Z\"l&p W5d&).o\"!#!\"' !\"%$! ! ! $ /\"8 !\"! I\")-##!\"!\"!(!,#\"! !,b%$9#(#O!+3@%M3)PN<Y!.!G&R!_CBL7iD2\"%6#4#E!F^$g=!Q0]>'& 1\"0 2\"%\"c\"$&5?+,/+& *4'6*J!7qKsHm#'\"! k!r h - $\"1\"!");

    /**
     * All "programming word" characters, a weird group that includes all letters, all numbers, and the underscore '_'.
     * You may want {@link #Identifier}, {@link #IdentifierPart}, and/or {@link #IdentifierStart} for Java identifier
     * characters, which are a larger group and allow matching the initial character differently from the rest.
     * <br>
     * Accessible in regexes via "<code>\w</code>" when Unicode mode hasn't been explicitly disabled.
     */
    public static final Category Word=new Category(new int[]{2,3,0,4,5,1,6,9,7,8,11,10,12,15,13,14,17,18,19,25,48,21,22,30,37,42,35,40,16,20,23,26,27,33,46,54,28,31,38,39,45,52,53,55,57,58,62,69,73,75,85,88,116,24,29,32,34,41,43,44,47,50,51,56,59,63,64,65,66,67,68,72,74,77,79,82,83,89,93,100,101,102,105,107,114,115,122,128,132,134,138,165,249,268,282,321,332,362,365,457,470,619,631,727,1133,1164,6581,8453,11171,20975},"4')3$\" 34\")% \"#%   6 7 \203$*-#)\" \"1T %!! \"(\"   \" 2 k z #!{ 8!\"(;'[ \" % % \"'?$!7+&P$p (!' 1!\"0M!o-J$\"!\"!H2@$+C= (6/ w!' 1 (!%!5 & \"#!!)!%!!'\"$% #!*!$!\" \"!  $$%!5 & % % %!\" #$%! #\")! \")-,  )   5 & % #!'    !\"<!!'+&   (!%!5 & % #!)!%! '%$% #!' &*% $#  !#% \" %#%# #*$##  !!\"(\"-,/,   6 -#(   !)%  &!!''& ! (   6 ' #!)   !)%)\" !!' %/! (   ]   #&-!1 $!% 0#> ) \"!&#\"$$ \" (('!%/L&/ ';% \"!% \"!\"(! &   \" \"!% ,  !# \" $!'!!A\"U%(2 \" \" \"$' :$2 0 :+\"MP(i!8 \"&\"!9 \200 !!& \" !!; !!W !!& \" !!/ _ !!d! +2#-0R!$#\205!< 3&h#+), &,=,2.,   %.l#\"$%!'('1 !'(S)9&O*7 *$**G!#,Z$3(+F@$N D!+('/\"'.eQ$'1).u.K''#4!))9! 0  8(| ~!$!8!$!( \" \" \" 7!I & \"#  &#!!$$,&  &T%!$&+(,I,$\"#*1\"$\"!' \"##(\" \" \" ! +!!&#$\" L\207`j5\206V\210B B x()+\"!8 \"&\"!K)\"<>+& & & & & & & & E4\"\204 ?/ #!#$R!%!  m !&9 n#!*?C-A'E( /A';/\177\212Q\2150\211fH!}#@5\\$' tF)!q!BN4'$*^.O*'(>#\" 4!:.D#b-+(7 C+.!'(6#g3 !-!#*$!$!$+& & 9 '*v %!'(\214.6$4\213\202!rG&.#&* , # \" % % sX\2012a!JY*$-0-c# y=')3(3,S#$!$!$! :");

    /**
     * All valid characters that can be used in a Java identifier.
     * <br>
     * Accessible in regexes via "<code>\pJ</code>" or "<code>\p{J}</code>" (J for Java).
     */
    public static final Category Identifier=new Category(new int[]{2,3,0,4,5,1,6,9,7,8,12,10,11,15,13,25,14,17,19,18,21,22,48,37,42,26,30,40,16,20,23,27,28,31,33,35,46,54,32,38,39,53,55,57,58,62,67,69,73,75,85,88,24,29,34,41,43,44,45,47,50,51,52,56,59,63,64,66,68,72,74,77,79,82,83,89,93,100,101,102,105,107,114,115,116,122,128,132,134,138,165,249,268,282,321,332,362,365,457,470,619,631,727,1133,1164,6581,8453,11171,20975},"\")&.'\"*')/$\" /$F!!$\")% \"#%   5 : \202$,-#)\" \"3t %!! \"(\"   \" 2 i y #!z 7!\"(;(\" Y \" % % \"'9$!/\"$+&P$n (!' 3!\"1L!m-I$\"!62?$+E= (50 v!' 3 (!%!4 & \"#!!)!%!!'\"$% #!2 % \"!  $$%!4 & % % %!\" #$%! #\")! \")-*  )   4 & % #!'    !\"<!!' \")&   (!%!4 & % #!)!%! '%$% #!' &,% $#  !#% \" %#%# #,$##  !!\"(\"-*(\"(*   5 -#(   !)%  &!!''& ! (   5 ' #!)   !)%)\" !!' %0! (   \\   #&-!3 $!% 1#> ) \"!&#\"$$ \" (('!%0K$- ';% \"!% \"!\"(! &   \" \"!% *  !# \" $!'!!B\"T%(2 \" \" \"$' C$2 1 C+\"LP(g!7 \"&\"!8 \177 !!& \" !!; !!F !!& \" !!0 _ !!c! +2#-1R!$#\204!< /&f#+)* &*=*2.*   %.j#\"# !'('3 !'(S)8&O,: ,$,,H!#*X$/(+G?$M @!+('0\"'.NQ$'3).s.J''#6!))8! 1  7({ }!$!7!$!( \" \" \" :!^ & \"#  &#!!$$*&  &N%=\"@%!$&+(*#A1*$\"#,3\"$\"!' \"##(\" \" \" ! +!!&#$\" K\206`h4\205U\207D D w()+\"!7 \"&\"!J)\"<>+& & & & & & & & A6\"\203 90 #!#$R!%!  k !&8 l#!,9E-B'A( 0B';0~\211Q\2141\210dZ!|#?4[$' rG)!o!DM6'$!\")].O,'(>#\" 6!C.@#b-+(: E+.!'(5#e/ !-!#,$!$!$+& & 8 ',u %!'(\213.5$6\212\201!pH&.#&, * # \" % % qV\2002a!IW*#-1-#%/ 9\"(# x)\"*')/$\" /*S#$!$!$! #%#%/");

    /**
     * All valid characters that can be used as the first character in a Java identifier (more than you might think).
     * <br>
     * Accessible in regexes via "<code>\p{Js}</code>".
     */
    public static final Category IdentifierStart=new Category(new int[]{2,0,3,4,1,6,5,8,7,12,17,10,25,11,15,22,9,16,21,42,29,40,30,46,13,18,19,26,14,37,20,23,24,27,31,32,33,35,36,55,88,28,41,43,49,51,52,53,54,56,59,63,65,67,68,85,34,38,39,45,47,48,50,62,64,66,69,71,74,75,79,81,82,83,89,93,94,98,102,105,107,114,116,130,132,134,138,165,191,268,277,332,362,365,457,470,513,619,1164,2680,6581,8453,11171,20975},"F!4,&! ,5\"&!-!&!%/ 6 ~&-.#'! !s# $\"\" !(!   ! : h v0w =\"!(5(!T;&\",!23F$ m !1$'$- \"!*! 46H)!,C+$&!#?&!+!#!@@'+P> (cO#!:!'01.&(\"$\"2 % !#\"#!*!<$  .\"'$0&&$\"2 % $ $ $C\" !> *'   2 % $ ##!:!1$1!'!)(\"$\"2 % $ ##!B$  1!9! &#  \"#$ ! $#$# #-?!J!)(   / .#!A %$B!&(   / 0 ##!D! $1$:(   5\"!*!% 0 ,&%*#? ' !\"%R\\ $)(R$ !\"$ !\"!(\" %   ! !\"$ \" $+!\"# !/\"D!`( EI#r32!*&&\"#!#$' &)8!9= !%!\"3 { \"\"% ! \"\"5 \"\"C \"\"% ! \"\"< Q \"\"aY.*W\"&#\201\"1 ,%d#+') \".*.*.)  1MF!#$VH'#\"D !%b-6^4\"#)K&,G/+Ni!l79%Q4<$-KAE3 -E\"''3\" 3\" \"#$+xTz\"&\"=\"&\"( ! ! ! 6\"N % !#  %#\"\"&&)%  %U$>!4!<!*)#BU!&!\"0 !##(! ! ! \" +\"\"%#&!95\2037 7 t(\"#$8= !%!\"G'!*/+% % % % % % % %g!\177 ;''#\"#&W(  j \"%3 k9;P.\200\204e\207*\202V[\"y#.-$27*6\"f5'\"n\"7_+   \" //!'M.LS&#! $)A-/;I'74!*# 0-# 5@  (2/#!#L !#$\"#\"! !, \"+' 8&\"&\"&+% % 3 0-q6\2068/&]\205}\"oZ%8#%! 0 ) # ! $ $ pX|:S\"OJ)G$, ;!(# u'!4,&! ,)H#&\"&\"&\" #$#$,");
    /**
     * All valid characters that can be used as the second or later character in a Java identifier.
     * <br>
     * Accessible in regexes via "<code>\p{Jp}</code>".
     */
    public static final Category IdentifierPart=new Category(new int[]{2,3,0,4,5,1,6,9,7,8,12,10,11,15,13,25,14,17,19,18,21,22,48,37,42,26,30,40,16,20,23,27,28,31,33,35,46,54,32,38,39,53,55,57,58,62,67,69,73,75,85,88,24,29,34,41,43,44,45,47,50,51,52,56,59,63,64,66,68,72,74,77,79,82,83,89,93,100,101,102,105,107,114,115,116,122,128,132,134,138,165,249,268,282,321,332,362,365,457,470,619,631,727,1133,1164,6581,8453,11171,20975},"\")&.'\"*')/$\" /$F!!$\")% \"#%   5 : \202$,-#)\" \"3t %!! \"(\"   \" 2 i y #!z 7!\"(;(\" Y \" % % \"'9$!/\"$+&P$n (!' 3!\"1L!m-I$\"!62?$+E= (50 v!' 3 (!%!4 & \"#!!)!%!!'\"$% #!2 % \"!  $$%!4 & % % %!\" #$%! #\")! \")-*  )   4 & % #!'    !\"<!!' \")&   (!%!4 & % #!)!%! '%$% #!' &,% $#  !#% \" %#%# #,$##  !!\"(\"-*(\"(*   5 -#(   !)%  &!!''& ! (   5 ' #!)   !)%)\" !!' %0! (   \\   #&-!3 $!% 1#> ) \"!&#\"$$ \" (('!%0K$- ';% \"!% \"!\"(! &   \" \"!% *  !# \" $!'!!B\"T%(2 \" \" \"$' C$2 1 C+\"LP(g!7 \"&\"!8 \177 !!& \" !!; !!F !!& \" !!0 _ !!c! +2#-1R!$#\204!< /&f#+)* &*=*2.*   %.j#\"# !'('3 !'(S)8&O,: ,$,,H!#*X$/(+G?$M @!+('0\"'.NQ$'3).s.J''#6!))8! 1  7({ }!$!7!$!( \" \" \" :!^ & \"#  &#!!$$*&  &N%=\"@%!$&+(*#A1*$\"#,3\"$\"!' \"##(\" \" \" ! +!!&#$\" K\206`h4\205U\207D D w()+\"!7 \"&\"!J)\"<>+& & & & & & & & A6\"\203 90 #!#$R!%!  k !&8 l#!,9E-B'A( 0B';0~\211Q\2141\210dZ!|#?4[$' rG)!o!DM6'$!\")].O,'(>#\" 6!C.@#b-+(: E+.!'(5#e/ !-!#,$!$!$+& & 8 ',u %!'(\213.5$6\212\201!pH&.#&, * # \" % % qV\2002a!IW*#-1-#%/ 9\"(# x)\"*')/$\" /*S#$!$!$! #%#%/");

    /**
     * Horizontal whitespace characters; not an actual Unicode category but probably more useful because it contains
     * the horizontal tab character while Unicode's {@link #Z} category does not.
     * <br>
     * Accessible in regexes via "<code>\p{Zh}</code>" or "<code>\p{Gh}</code>" .
     */
    public static final Category Horizontal = new Category(new int[]{0,9,10,23,37,48,128,2432,4001,5600},"! # & ) '\"$ % ( ");

    /**
     * Vertical whitespace characters; not an actual Unicode category but probably more useful because it contains the
     * newline and carriage return characters while Unicode's {@link #Z} category does not.
     * <br>
     * Accessible in regexes via "<code>\p{Zv}</code>" or "<code>\p{Gv}</code>" .
     */
    public static final Category Vertical = new Category(new int[]{0,1,3,10,120,8099},"#\"$ %!");

    /**
     * Whitespace characters, both horizontal and vertical; not an actual Unicode category but acts like the combination
     * of {@link #Horizontal} (Zh in regexes) and {@link #Vertical} (Zv in regexes) in that it includes both the obscure
     * Unicode characters that are in the Unicode Z category but also the practical characters such as carriage return,
     * newline, and horizontal tab that are not classified as whitespace by Unicode but are by everyone else.
     * <br>
     * Accessible in regexes via "<code>\pG</code>" or "<code>\p{G}</code>" (G for Gap).
     */
    public static final Category Space=new Category(new int[]{0,1,4,6,9,10,19,27,30,48,101,2432,4001,5600},"$\"& * ' - +%(!# ) , ");

    private static final char[] upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZµÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖØÙÚÛÜÝÞĀĂĄĆĈĊČĎĐĒĔĖĘĚĜĞĠĢĤĦĨĪĬĮĲĴĶĹĻĽĿŁŃŅŇŊŌŎŐŒŔŖŘŚŜŞŠŢŤŦŨŪŬŮŰŲŴŶŸŹŻŽſƁƂƄƆƇƉƊƋƎƏƐƑƓƔƖƗƘƜƝƟƠƢƤƦƧƩƬƮƯƱƲƳƵƷƸƼǄǅǇǈǊǋǍǏǑǓǕǗǙǛǞǠǢǤǦǨǪǬǮǱǲǴǶǷǸǺǼǾȀȂȄȆȈȊȌȎȐȒȔȖȘȚȜȞȠȢȤȦȨȪȬȮȰȲȺȻȽȾɁɃɄɅɆɈɊɌɎͅͰͲͶͿΆΈΉΊΌΎΏΑΒΓΔΕΖΗΘΙΚΛΜΝΞΟΠΡΣΣΤΥΦΧΨΩΪΫϏϐϑϕϖϘϚϜϞϠϢϤϦϨϪϬϮϰϱϴϵϷϹϺϽϾϿЀЁЂЃЄЅІЇЈЉЊЋЌЍЎЏАБВГДЕЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯѠѢѤѦѨѪѬѮѰѲѴѶѸѺѼѾҀҊҌҎҐҒҔҖҘҚҜҞҠҢҤҦҨҪҬҮҰҲҴҶҸҺҼҾӀӁӃӅӇӉӋӍӐӒӔӖӘӚӜӞӠӢӤӦӨӪӬӮӰӲӴӶӸӺӼӾԀԂԄԆԈԊԌԎԐԒԔԖԘԚԜԞԠԢԤԦԨԪԬԮԱԲԳԴԵԶԷԸԹԺԻԼԽԾԿՀՁՂՃՄՅՆՇՈՉՊՋՌՍՎՏՐՑՒՓՔՕՖႠႡႢႣႤႥႦႧႨႩႪႫႬႭႮႯႰႱႲႳႴႵႶႷႸႹႺႻႼႽႾႿჀჁჂჃჄჅჇჍᏸᏹᏺᏻᏼᏽᲀᲁᲂᲃᲄᲅᲆᲇᲈᲐᲑᲒᲓᲔᲕᲖᲗᲘᲙᲚᲛᲜᲝᲞᲟᲠᲡᲢᲣᲤᲥᲦᲧᲨᲩᲪᲫᲬᲭᲮᲯᲰᲱᲲᲳᲴᲵᲶᲷᲸᲹᲺᲽᲾᲿḀḂḄḆḈḊḌḎḐḒḔḖḘḚḜḞḠḢḤḦḨḪḬḮḰḲḴḶḸḺḼḾṀṂṄṆṈṊṌṎṐṒṔṖṘṚṜṞṠṢṤṦṨṪṬṮṰṲṴṶṸṺṼṾẀẂẄẆẈẊẌẎẐẒẔẛẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼẾỀỂỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪỬỮỰỲỴỶỸỺỼỾἈἉἊἋἌἍἎἏἘἙἚἛἜἝἨἩἪἫἬἭἮἯἸἹἺἻἼἽἾἿὈὉὊὋὌὍὙὛὝὟὨὩὪὫὬὭὮὯᾸᾹᾺΆιῈΈῊΉῘῙῚΊῨῩῪΎῬῸΌῺΏΩKÅℲⅠⅡⅢⅣⅤⅥⅦⅧⅨⅩⅪⅫⅬⅭⅮⅯↃⒶⒷⒸⒹⒺⒻⒼⒽⒾⒿⓀⓁⓂⓃⓄⓅⓆⓇⓈⓉⓊⓋⓌⓍⓎⓏⰀⰁⰂⰃⰄⰅⰆⰇⰈⰉⰊⰋⰌⰍⰎⰏⰐⰑⰒⰓⰔⰕⰖⰗⰘⰙⰚⰛⰜⰝⰞⰟⰠⰡⰢⰣⰤⰥⰦⰧⰨⰩⰪⰫⰬⰭⰮⱠⱢⱣⱤⱧⱩⱫⱭⱮⱯⱰⱲⱵⱾⱿⲀⲂⲄⲆⲈⲊⲌⲎⲐⲒⲔⲖⲘⲚⲜⲞⲠⲢⲤⲦⲨⲪⲬⲮⲰⲲⲴⲶⲸⲺⲼⲾⳀⳂⳄⳆⳈⳊⳌⳎⳐⳒⳔⳖⳘⳚⳜⳞⳠⳢⳫⳭⳲꙀꙂꙄꙆꙈꙊꙌꙎꙐꙒꙔꙖꙘꙚꙜꙞꙠꙢꙤꙦꙨꙪꙬꚀꚂꚄꚆꚈꚊꚌꚎꚐꚒꚔꚖꚘꚚꜢꜤꜦꜨꜪꜬꜮꜲꜴꜶꜸꜺꜼꜾꝀꝂꝄꝆꝈꝊꝌꝎꝐꝒꝔꝖꝘꝚꝜꝞꝠꝢꝤꝦꝨꝪꝬꝮꝹꝻꝽꝾꞀꞂꞄꞆꞋꞍꞐꞒꞖꞘꞚꞜꞞꞠꞢꞤꞦꞨꞪꞫꞬꞭꞮꞰꞱꞲꞳꞴꞶꞸꭰꭱꭲꭳꭴꭵꭶꭷꭸꭹꭺꭻꭼꭽꭾꭿꮀꮁꮂꮃꮄꮅꮆꮇꮈꮉꮊꮋꮌꮍꮎꮏꮐꮑꮒꮓꮔꮕꮖꮗꮘꮙꮚꮛꮜꮝꮞꮟꮠꮡꮢꮣꮤꮥꮦꮧꮨꮩꮪꮫꮬꮭꮮꮯꮰꮱꮲꮳꮴꮵꮶꮷꮸꮹꮺꮻꮼꮽꮾꮿＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ𐐀𐐁𐐂𐐃𐐄𐐅𐐆𐐇𐐈𐐉𐐊𐐋𐐌𐐍𐐎𐐏𐐐𐐑𐐒𐐓𐐔𐐕𐐖𐐗𐐘𐐙𐐚𐐛𐐜𐐝𐐞𐐟𐐠𐐡𐐢𐐣𐐤𐐥𐐦𐐧𐒰𐒱𐒲𐒳𐒴𐒵𐒶𐒷𐒸𐒹𐒺𐒻𐒼𐒽𐒾𐒿𐓀𐓁𐓂𐓃𐓄𐓅𐓆𐓇𐓈𐓉𐓊𐓋𐓌𐓍𐓎𐓏𐓐𐓑𐓒𐓓𐲀𐲁𐲂𐲃𐲄𐲅𐲆𐲇𐲈𐲉𐲊𐲋𐲌𐲍𐲎𐲏𐲐𐲑𐲒𐲓𐲔𐲕𐲖𐲗𐲘𐲙𐲚𐲛𐲜𐲝𐲞𐲟𐲠𐲡𐲢𐲣𐲤𐲥𐲦𐲧𐲨𐲩𐲪𐲫𐲬𐲭𐲮𐲯𐲰𐲱𐲲𑢠𑢡𑢢𑢣𑢤𑢥𑢦𑢧𑢨𑢩𑢪𑢫𑢬𑢭𑢮𑢯𑢰𑢱𑢲𑢳𑢴𑢵𑢶𑢷𑢸𑢹𑢺𑢻𑢼𑢽𑢾𑢿𖹀𖹁𖹂𖹃𖹄𖹅𖹆𖹇𖹈𖹉𖹊𖹋𖹌𖹍𖹎𖹏𖹐𖹑𖹒𖹓𖹔𖹕𖹖𖹗𖹘𖹙𖹚𖹛𖹜𖹝𖹞𖹟𞤀𞤁𞤂𞤃𞤄𞤅𞤆𞤇𞤈𞤉𞤊𞤋𞤌𞤍𞤎𞤏𞤐𞤑𞤒𞤓𞤔𞤕𞤖𞤗𞤘𞤙𞤚𞤛𞤜𞤝𞤞𞤟𞤠𞤡ẞᾈᾉᾊᾋᾌᾍᾎᾏᾘᾙᾚᾛᾜᾝᾞᾟᾨᾩᾪᾫᾬᾭᾮᾯᾼῌῼ".toCharArray();
    private static final char[] lower = "abcdefghijklmnopqrstuvwxyzμàáâãäåæçèéêëìíîïðñòóôõöøùúûüýþāăąćĉċčďđēĕėęěĝğġģĥħĩīĭįĳĵķĺļľŀłńņňŋōŏőœŕŗřśŝşšţťŧũūŭůűųŵŷÿźżžsɓƃƅɔƈɖɗƌǝəɛƒɠɣɩɨƙɯɲɵơƣƥʀƨʃƭʈưʊʋƴƶʒƹƽǆǆǉǉǌǌǎǐǒǔǖǘǚǜǟǡǣǥǧǩǫǭǯǳǳǵƕƿǹǻǽǿȁȃȅȇȉȋȍȏȑȓȕȗșțȝȟƞȣȥȧȩȫȭȯȱȳⱥȼƚⱦɂƀʉʌɇɉɋɍɏιͱͳͷϳάέήίόύώαβγδεζηθικλμνξοπρςστυφχψωϊϋϗβθφπϙϛϝϟϡϣϥϧϩϫϭϯκρθεϸϲϻͻͼͽѐёђѓєѕіїјљњћќѝўџабвгдежзийклмнопрстуфхцчшщъыьэюяѡѣѥѧѩѫѭѯѱѳѵѷѹѻѽѿҁҋҍҏґғҕҗҙқҝҟҡңҥҧҩҫҭүұҳҵҷҹһҽҿӏӂӄӆӈӊӌӎӑӓӕӗәӛӝӟӡӣӥӧөӫӭӯӱӳӵӷӹӻӽӿԁԃԅԇԉԋԍԏԑԓԕԗԙԛԝԟԡԣԥԧԩԫԭԯաբգդեզէըթժիլխծկհձղճմյնշոչպջռսվտրցւփքօֆⴀⴁⴂⴃⴄⴅⴆⴇⴈⴉⴊⴋⴌⴍⴎⴏⴐⴑⴒⴓⴔⴕⴖⴗⴘⴙⴚⴛⴜⴝⴞⴟⴠⴡⴢⴣⴤⴥⴧⴭᏰᏱᏲᏳᏴᏵвдосттъѣꙋაბგდევზთიკლმნოპჟრსტუფქღყშჩცძწჭხჯჰჱჲჳჴჵჶჷჸჹჺჽჾჿḁḃḅḇḉḋḍḏḑḓḕḗḙḛḝḟḡḣḥḧḩḫḭḯḱḳḵḷḹḻḽḿṁṃṅṇṉṋṍṏṑṓṕṗṙṛṝṟṡṣṥṧṩṫṭṯṱṳṵṷṹṻṽṿẁẃẅẇẉẋẍẏẑẓẕṡạảấầẩẫậắằẳẵặẹẻẽếềểễệỉịọỏốồổỗộớờởỡợụủứừửữựỳỵỷỹỻỽỿἀἁἂἃἄἅἆἇἐἑἒἓἔἕἠἡἢἣἤἥἦἧἰἱἲἳἴἵἶἷὀὁὂὃὄὅὑὓὕὗὠὡὢὣὤὥὦὧᾰᾱὰάιὲέὴήῐῑὶίῠῡὺύῥὸόὼώωkåⅎⅰⅱⅲⅳⅴⅵⅶⅷⅸⅹⅺⅻⅼⅽⅾⅿↄⓐⓑⓒⓓⓔⓕⓖⓗⓘⓙⓚⓛⓜⓝⓞⓟⓠⓡⓢⓣⓤⓥⓦⓧⓨⓩⰰⰱⰲⰳⰴⰵⰶⰷⰸⰹⰺⰻⰼⰽⰾⰿⱀⱁⱂⱃⱄⱅⱆⱇⱈⱉⱊⱋⱌⱍⱎⱏⱐⱑⱒⱓⱔⱕⱖⱗⱘⱙⱚⱛⱜⱝⱞⱡɫᵽɽⱨⱪⱬɑɱɐɒⱳⱶȿɀⲁⲃⲅⲇⲉⲋⲍⲏⲑⲓⲕⲗⲙⲛⲝⲟⲡⲣⲥⲧⲩⲫⲭⲯⲱⲳⲵⲷⲹⲻⲽⲿⳁⳃⳅⳇⳉⳋⳍⳏⳑⳓⳕⳗⳙⳛⳝⳟⳡⳣⳬⳮⳳꙁꙃꙅꙇꙉꙋꙍꙏꙑꙓꙕꙗꙙꙛꙝꙟꙡꙣꙥꙧꙩꙫꙭꚁꚃꚅꚇꚉꚋꚍꚏꚑꚓꚕꚗꚙꚛꜣꜥꜧꜩꜫꜭꜯꜳꜵꜷꜹꜻꜽꜿꝁꝃꝅꝇꝉꝋꝍꝏꝑꝓꝕꝗꝙꝛꝝꝟꝡꝣꝥꝧꝩꝫꝭꝯꝺꝼᵹꝿꞁꞃꞅꞇꞌɥꞑꞓꞗꞙꞛꞝꞟꞡꞣꞥꞧꞩɦɜɡɬɪʞʇʝꭓꞵꞷꞹᎠᎡᎢᎣᎤᎥᎦᎧᎨᎩᎪᎫᎬᎭᎮᎯᎰᎱᎲᎳᎴᎵᎶᎷᎸᎹᎺᎻᎼᎽᎾᎿᏀᏁᏂᏃᏄᏅᏆᏇᏈᏉᏊᏋᏌᏍᏎᏏᏐᏑᏒᏓᏔᏕᏖᏗᏘᏙᏚᏛᏜᏝᏞᏟᏠᏡᏢᏣᏤᏥᏦᏧᏨᏩᏪᏫᏬᏭᏮᏯａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚ𐐨𐐩𐐪𐐫𐐬𐐭𐐮𐐯𐐰𐐱𐐲𐐳𐐴𐐵𐐶𐐷𐐸𐐹𐐺𐐻𐐼𐐽𐐾𐐿𐑀𐑁𐑂𐑃𐑄𐑅𐑆𐑇𐑈𐑉𐑊𐑋𐑌𐑍𐑎𐑏𐓘𐓙𐓚𐓛𐓜𐓝𐓞𐓟𐓠𐓡𐓢𐓣𐓤𐓥𐓦𐓧𐓨𐓩𐓪𐓫𐓬𐓭𐓮𐓯𐓰𐓱𐓲𐓳𐓴𐓵𐓶𐓷𐓸𐓹𐓺𐓻𐳀𐳁𐳂𐳃𐳄𐳅𐳆𐳇𐳈𐳉𐳊𐳋𐳌𐳍𐳎𐳏𐳐𐳑𐳒𐳓𐳔𐳕𐳖𐳗𐳘𐳙𐳚𐳛𐳜𐳝𐳞𐳟𐳠𐳡𐳢𐳣𐳤𐳥𐳦𐳧𐳨𐳩𐳪𐳫𐳬𐳭𐳮𐳯𐳰𐳱𐳲𑣀𑣁𑣂𑣃𑣄𑣅𑣆𑣇𑣈𑣉𑣊𑣋𑣌𑣍𑣎𑣏𑣐𑣑𑣒𑣓𑣔𑣕𑣖𑣗𑣘𑣙𑣚𑣛𑣜𑣝𑣞𑣟𖹠𖹡𖹢𖹣𖹤𖹥𖹦𖹧𖹨𖹩𖹪𖹫𖹬𖹭𖹮𖹯𖹰𖹱𖹲𖹳𖹴𖹵𖹶𖹷𖹸𖹹𖹺𖹻𖹼𖹽𖹾𖹿𞤢𞤣𞤤𞤥𞤦𞤧𞤨𞤩𞤪𞤫𞤬𞤭𞤮𞤯𞤰𞤱𞤲𞤳𞤴𞤵𞤶𞤷𞤸𞤹𞤺𞤻𞤼𞤽𞤾𞤿𞥀𞥁𞥂𞥃ßᾀᾁᾂᾃᾄᾅᾆᾇᾐᾑᾒᾓᾔᾕᾖᾗᾠᾡᾢᾣᾤᾥᾦᾧᾳῃῳ".toCharArray();
    private static final CharCharMap upperToLower = new CharCharMap(upper, lower);
    private static final CharCharMap lowerToUpper = new CharCharMap(lower, upper);

    private static final char[] openers =
            new char[]{'(','<','[','{','༺','༼','᚛','⁅','⁽','₍','⌈','⌊','〈','❨','❪','❬','❮','❰','❲','❴','⟅','⟦',
                    '⟨','⟪','⟬','⟮','⦃','⦅','⦇','⦉','⦋','⦍','⦏','⦑','⦓','⦕','⦗','⧘','⧚','⧼','⸢','⸤','⸦','⸨',
                    '〈','《','「','『','【','〔','〖','〘','〚','〝','﴿','︗','︵','︷','︹','︻','︽','︿','﹁',
                    '﹃','﹇','﹙','﹛','﹝','（','［','｛','｟','｢'},
    closers =
            new char[]{')','>',']','}','༻','༽','᚜','⁆','⁾','₎','⌉','⌋','〉','❩','❫','❭','❯','❱','❳','❵','⟆','⟧',
                    '⟩','⟫','⟭','⟯','⦄','⦆','⦈','⦊','⦌','⦎','⦐','⦒','⦔','⦖','⦘','⧙','⧛','⧽','⸣','⸥','⸧','⸩',
                    '〉','》','」','』','】','〕','〗','〙','〛','〞','﴾','︘','︶','︸','︺','︼','︾','﹀','﹂',
                    '﹄','﹈','﹚','﹜','﹞','）','］','｝','｠','｣'};

    private static final CharCharMap openBrackets = new CharCharMap(openers, closers),
            closingBrackets = new CharCharMap(closers, openers);

    /**
     * Returns the given char c's lower-case representation, if it has one, otherwise returns it verbatim.
     * @param c any char; this should only return a case-folded different char for upper-case letters
     * @return the single-char case-folded version of c, of it has one, otherwise c
     */
    public static char caseFold(char c)
    {
        if(upperToLower.containsKey(c))
        {
            return upperToLower.get(c);
        }
        return c;
    }

    /**
     * The counterpart to {@link #caseFold(char)} hat returns the given char c's upper-case representation, if it has
     * one, otherwise it returns it verbatim. This has dubiously correct behavior for digraphs and ligature chars, but
     * they tend to be rare or even discouraged in practice.
     * @param c any char; this should only return a case-folded different char for lower-case letters
     * @return the single-char upper-case version of c, if it has one, otherwise c
     */
    public static char caseUp(char c)
    {
        if(lowerToUpper.containsKey(c))
        {
            return lowerToUpper.get(c);
        }
        return c;
    }

    /**
     * Finds the matching closing or opening bracket when given an opening or closing bracket as the char c. If c is not
     * a bracket character this recognizes, then this will return c verbatim; you can check if the return value of this
     * method is equal to c to determine if a matching bracket char is possible. This does recognize '&lt;' as opening
     * and '&gt;' as closing, despite those two not being in Unicode's categories of opening or closing brackets,
     * because more programmers should find that behavior useful and matching always should need to be specified anyway
     * (you won't have '&lt;' or '&gt;' change meaning unless you're expecting a matching bracket).
     * @param c any char; if it is a bracket this will different behavior than non-bracket characters
     * @return a char; if c is a bracket this will return its opening or closing counterpart, otherwise returns c
     */
    public static char matchBracket(char c)
    {
        if(openBrackets.containsKey(c))
        {
            return openBrackets.get(c);
        }
        else if(closingBrackets.containsKey(c))
        {
            return closingBrackets.get(c);
        }
        return c;
    }

    public static String reverseWithBrackets(CharSequence s)
    {
        char[] c = new char[s.length()];
        for (int i = c.length - 1, r = 0; i >= 0; i--, r++) {
            c[r] = matchBracket(s.charAt(i));
        }
        return String.valueOf(c);
    }

    public static boolean reverseEqual(String left, String right)
    {
        if(left == null) return right == null;
        if(right == null) return false;
        if(left.length() != right.length()) return false;
        for (int l = 0, r = right.length() - 1; r >= 0; r--, l++) {
            if(left.charAt(l) != right.charAt(r)) return false;
        }
        return true;
    }

    public static boolean reverseBracketEqual(String left, String right)
    {
        if(left == null) return right == null;
        if(right == null) return false;
        if(left.length() != right.length()) return false;
        for (int l = 0, r = right.length() - 1; r >= 0; r--, l++) {
            if(left.charAt(l) != matchBracket(right.charAt(r))) return false;
        }
        return true;
    }

    public static final LinkedHashMap<String, Category> categories;
    public static final LinkedHashMap<String, Category> superCategories;
    static {

        superCategories = new LinkedHashMap<String, Category>(16);
        superCategories.put("C", C);
        superCategories.put("L", L);
        superCategories.put("M", M);
        superCategories.put("N", N);
        superCategories.put("Z", Z);
        superCategories.put("P", P);
        superCategories.put("S", S);
        superCategories.put("J", Identifier);
        superCategories.put("G", Space);

        categories = new LinkedHashMap<String, Category>(64);
        categories.put("C", C);
        categories.put("L", L);
        categories.put("M", M);
        categories.put("N", N);
        categories.put("Z", Z);
        categories.put("P", P);
        categories.put("S", S);
        categories.put("J", Identifier);
        categories.put("G", Space);
        categories.put("Cc", Cc);
        categories.put("Cf", Cf);
        categories.put("Co", Co);
        categories.put("Cn", Cn);
        categories.put("Cs", Cs);
        categories.put("Lu", Lu);
        categories.put("Ll", Ll);
        categories.put("Lt", Lt);
        categories.put("Lm", Lm);
        categories.put("Lo", Lo);
        categories.put("Mn", Mn);
        categories.put("Me", Me);
        categories.put("Mc", Mc);
        categories.put("Nd", Nd);
        categories.put("Nl", Nl);
        categories.put("No", No);
        categories.put("Zs", Zs);
        categories.put("Zl", Zl);
        categories.put("Zp", Zp);
        categories.put("Pd", Pd);
        categories.put("Ps", Ps);
        categories.put("Pi", Pi);
        categories.put("Pe", Pe);
        categories.put("Pf", Pf);
        categories.put("Pc", Pc);
        categories.put("Po", Po);
        categories.put("Sm", Sm);
        categories.put("Sc", Sc);
        categories.put("Sk", Sk);
        categories.put("So", So);
        categories.put("Zh", Horizontal);
        categories.put("Zv", Vertical);
        categories.put("Gh", Horizontal);
        categories.put("Gv", Vertical);
        categories.put("Js", IdentifierStart);
        categories.put("Jp", IdentifierPart);
    }
}
