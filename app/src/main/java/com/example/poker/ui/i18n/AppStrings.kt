package com.example.poker.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

interface AppStrings {
    val languageCode: String

    // Navigation
    val navTable: String
    val navAnalytics: String
    val navPlayers: String

    // Table Screen Header & General
    val handNumber: String
    fun handNumber(num: Int): String = "$handNumber #$num"
    val totalPot: String
    val totalPotLabel: String
    fun blindsLabel(sb: Long, bb: Long): String
    fun blindsLabel(sb: Long, bb: Long, currency: String): String = "${blindsLabel(sb, bb)} $currency"
    fun anteLabel(ante: Long): String
    val currentTurn: String
    val startFirstHand: String
    val startNewHand: String
    val restart: String
    val restartBtn: String
    val restartGameTooltip: String
    val templatesTooltip: String
    val addPlayerTooltip: String
    val settingsTooltip: String
    val languageToggleTooltip: String
    val playersWithChips: String

    // Pots Display
    val mainPot: String
    fun sidePot(index: Int): String
    val eligiblePlayers: String
    val activeBetsOnTable: String
    val inPot: String
    val noActivePots: String
    val totalTablePot: String

    // Player Seat Card
    val dealerBadge: String
    val smallBlindBadge: String
    val bigBlindBadge: String
    val chipsLabel: String
    val buyInLabel: String
    val totalBuyInLabel: String
    val netProfitLossLabel: String
    val netPnlLabel: String
    val pnlLabel: String
    val rebuyBtn: String
    val adjustBtn: String
    val removeBtn: String
    val removePlayerLabel: String
    val playerTurnIndicator: String
    val betThisHandLabel: String
    val turnBanner: String

    // Betting Controls
    val turnPlayer: String
    fun turnPlayer(name: String): String = "$turnPlayer: $name"
    fun playerTurnTitle(name: String): String
    fun highestBetInfo(amount: Long, currency: String): String
    fun potInfo(amount: Long, currency: String): String
    fun minRaiseInfo(amount: Long, currency: String): String
    val fold: String
    val foldBtn: String
    val check: String
    val checkBtn: String
    val call: String
    fun callBtn(amount: Long, currency: String): String
    val bet: String
    fun betBtn(amount: Long, currency: String): String
    val raise: String
    fun raiseToBtn(amount: Long, currency: String): String
    val allIn: String
    fun allInBtn(amount: Long, currency: String): String
    val presetMin: String
    val presetHalfPot: String
    val presetPot: String
    val presetAllIn: String
    val betAmountLabel: String
    val raiseAmountLabel: String
    val quickMin: String
    val quick2x: String
    val quickHalfPot: String
    val quickPot: String
    val quickAllIn: String
    val confirmRaise: String

    // Add Player Dialog
    val addPlayerTitle: String
    val playerNameLabel: String
    val playerNamePlaceholder: String
    val buyInInputLabel: String
    val avatarColorLabel: String
    val addPlayerConfirm: String
    val cancel: String

    // Rebuy Dialog
    val rebuyTitle: String
    fun rebuyForPlayer(name: String): String
    val currentChips: String
    fun currentChips(amount: Long, currency: String): String = "$currentChips: $amount $currency"
    val rebuyAmountLabel: String
    val confirmRebuy: String

    // Adjust Chips Dialog
    val adjustChipsTitle: String
    val changeAmountLabel: String
    val changeAmountHint: String
    val reasonLabel: String
    val reasonPlaceholder: String
    fun chipsAfterAdjustment(amount: Long, currency: String): String
    val applyAdjustment: String

    // Settings Dialog
    val settingsTitle: String
    val languageLabel: String
    val languagePersian: String
    val languageEnglish: String
    val sbLabel: String
    val bbLabel: String
    val anteSettingLabel: String
    val autoPostBlindsLabel: String
    val currencyNameLabel: String
    val currencyNamePlaceholder: String
    val saveSettings: String
    fun versionLabel(version: String): String

    // Templates Dialog
    val templatesTitle: String
    val templatesSubtitle: String
    val saveCurrentState: String
    val saveCurrentAsTemplate: String
    val templateNameLabel: String
    val templateNamePlaceholder: String
    val templateNameHint: String
    val saveTemplateBtn: String
    val savedTemplatesTitle: String
    val savedTemplatesHeader: String
    fun savedTemplatesHeader(count: Int): String = "$savedTemplatesHeader ($count)"
    val noSavedTemplates: String
    val noTemplatesMessage: String
    val loadBtn: String
    val restoreBtn: String
    val updateBtn: String
    val deleteBtn: String
    val restoreTemplateTitle: String
    fun restoreTemplateTitle(name: String): String = "$restoreTemplateTitle: $name"
    val restoreTemplateBody: String
    fun restoreTemplateBody(players: Int, hands: Int): String
    val confirmRestoreBtn: String
    val updateTemplateTitle: String
    fun updateTemplateTitle(name: String): String = "$updateTemplateTitle: $name"
    val updateTemplateBody: String
    fun updateTemplateBody(name: String): String
    val confirmUpdateBtn: String
    val deleteTemplateTitle: String
    fun deleteTemplateTitle(name: String): String = "$deleteTemplateTitle: $name"
    val deleteTemplateBody: String
    val confirmDeleteBtn: String
    fun templatePlayerCount(count: Int): String
    fun templateTotalChips(amount: Long, currency: String): String

    // Restart Dialog
    val restartDialogTitle: String
    val restartConfirmQuestion: String
    val restartWarningDetail: String
    val confirmRestartBtn: String

    // Showdown Dialog
    val showdownTitle: String
    val showdownSubtitle: String
    val showdownSplitNote: String
    val selectAtLeastOneWinner: String
    val selectWinnersWarning: String
    val confirmWinnersAndEndHand: String
    val confirmAwardPots: String
    val evenSplit: String
    fun evenSplit(share: Long, currency: String): String = "$evenSplit: $share $currency"

    // Analytics Screen & Chart
    val analyticsTitle: String
    val analyticsSubtitle: String
    val handsPlayedStat: String
    val handsPlayed: String
    val totalPotsStat: String
    val totalBuyInStat: String
    val totalBuyIns: String
    val leaderboardTitle: String
    val topWinner: String
    val noProfit: String
    val playerCountLabel: String
    val initialBuyInStat: String
    val currentChipsStat: String
    val handsHistoryTitle: String
    val handLedgerTitle: String
    val noHandsHistory: String
    val noHandsPlayedYet: String
    val winnersLabel: String
    val potBreakdownLabel: String
    val playerChipChanges: String
    val chartTitle: String
    val chartSubtitle: String
    val balanceExact: String
    val balanceMismatch: String

    // Players Screen
    val playerManagementTitle: String
    val playersManagementTitle: String
    val playersManagementSubtitle: String
    val chipsInPlayStat: String
    val totalBuyInsStat: String
    val chipBalanceStat: String
    val chipBalanceOk: String
    fun chipBalanceError(diff: Long, currency: String): String
    val addNewPlayerBtn: String
    val noPlayersYet: String
    val chipTransactionsTitle: String
    val buyInHistoryTitle: String
    val initialBuyIn: String
    val initialBuyInTag: String
    val rebuyTag: String
    val rebuyHistory: String
    val manualAdjustmentTag: String
    val removePlayerConfirmTitle: String
    fun removePlayerConfirmMessage(name: String): String
    fun seatNumber(num: Int): String

    // First Run Language Dialog
    val initialLanguageTitle: String
    val initialLanguageSubtitle: String
    val persianOptionTitle: String
    val persianOptionDesc: String
    val englishOptionTitle: String
    val englishOptionDesc: String
    val confirmLanguageBtn: String

    // Raise Controls
    val raiseInputLabel: String
    fun stepSbInfo(amount: Long, currency: String): String
}

object PersianStrings : AppStrings {
    override val languageCode = "fa"

    override val navTable = "میز بازی"
    override val navAnalytics = "سود و زیان"
    override val navPlayers = "بازیکنان"

    override val handNumber = "دست"
    override fun handNumber(num: Int) = "دست #$num"
    override val totalPot = "پات کل"
    override val totalPotLabel = "مجموع پات"
    override fun blindsLabel(sb: Long, bb: Long) = "بلایندها: $sb / $bb"
    override fun blindsLabel(sb: Long, bb: Long, currency: String) = "بلایندها: $sb / $bb $currency"
    override fun anteLabel(ante: Long) = "آنته: $ante"
    override val currentTurn = "نوبت:"
    override val startFirstHand = "شروع اولین دست"
    override val startNewHand = "شروع دست جدید"
    override val restart = "ری‌استارت"
    override val restartBtn = "ری‌استارت"
    override val restartGameTooltip = "ری‌استارت بازی"
    override val templatesTooltip = "تمپلیت‌های بازی"
    override val addPlayerTooltip = "افزودن بازیکن"
    override val settingsTooltip = "تنظیمات بازی"
    override val languageToggleTooltip = "تغییر زبان"
    override val playersWithChips = "بازیکنان دارای چیپ"

    override val mainPot = "پات اصلی"
    override fun sidePot(index: Int) = "ساید پات $index"
    override val eligiblePlayers = "واجدین شرایط:"
    override val activeBetsOnTable = "شرط‌های جاری روی میز:"
    override val inPot = "در پات"
    override val noActivePots = "هنوز پاتی تشکیل نشده است"
    override val totalTablePot = "مجموع پات‌های روی میز"

    override val dealerBadge = "دیلر"
    override val smallBlindBadge = "SB"
    override val bigBlindBadge = "BB"
    override val chipsLabel = "موجودی چیپ"
    override val buyInLabel = "ورودی"
    override val totalBuyInLabel = "کل ورودی (Buy-in)"
    override val netProfitLossLabel = "سود/زیان"
    override val netPnlLabel = "سود / زیان"
    override val pnlLabel = "سود / زیان"
    override val rebuyBtn = "خرید مجدد"
    override val adjustBtn = "تنظیم دستی"
    override val removeBtn = "حذف"
    override val removePlayerLabel = "حذف بازیکن"
    override val playerTurnIndicator = "نوبت بازی"
    override val betThisHandLabel = "شرط این دست"
    override val turnBanner = "نوبت بازی این بازیکن است"

    override val turnPlayer = "نوبت بازیکن"
    override fun turnPlayer(name: String) = "نوبت بازیکن: $name"
    override fun playerTurnTitle(name: String) = "نوبت $name"
    override fun highestBetInfo(amount: Long, currency: String) = "بالاترین بت: $amount $currency"
    override fun potInfo(amount: Long, currency: String) = "پات: $amount $currency"
    override fun minRaiseInfo(amount: Long, currency: String) = "حداقل ریز: $amount $currency"
    override val fold = "فولد"
    override val foldBtn = "فولد"
    override val check = "چک"
    override val checkBtn = "چک"
    override val call = "کال"
    override fun callBtn(amount: Long, currency: String) = "کال $amount $currency"
    override val bet = "بت"
    override fun betBtn(amount: Long, currency: String) = "بت $amount $currency"
    override val raise = "ریز"
    override fun raiseToBtn(amount: Long, currency: String) = "ریز به $amount $currency"
    override val allIn = "آل‌این"
    override fun allInBtn(amount: Long, currency: String) = "آل‌این ($amount $currency)"
    override val presetMin = "حداقل"
    override val presetHalfPot = "½ پات"
    override val presetPot = "پات کامل"
    override val presetAllIn = "آل‌این"
    override val betAmountLabel = "مبلغ بت"
    override val raiseAmountLabel = "مبلغ ریز نهایی"
    override val quickMin = "حداقل"
    override val quick2x = "2X BB"
    override val quickHalfPot = "½ پات"
    override val quickPot = "پات کامل"
    override val quickAllIn = "آل‌این"
    override val confirmRaise = "ثبت ریز"

    override val addPlayerTitle = "افزودن بازیکن جدید"
    override val playerNameLabel = "نام بازیکن"
    override val playerNamePlaceholder = "مثلاً: علی"
    override val buyInInputLabel = "میزان ورودی و چیپ اولیه (Buy-in)"
    override val avatarColorLabel = "رنگ نشان بازیکن:"
    override val addPlayerConfirm = "افزودن بازیکن"
    override val cancel = "انصراف"

    override val rebuyTitle = "بای‌این مجدد (خرید چیپ)"
    override fun rebuyForPlayer(name: String) = "بازیکن: $name"
    override val currentChips = "موجودی چیپ فعلی"
    override fun currentChips(amount: Long, currency: String) = "موجودی فعلی: $amount $currency"
    override val rebuyAmountLabel = "مبلغ بای‌این جدید"
    override val confirmRebuy = "ثبت خرید چیپ"

    override val adjustChipsTitle = "تنظیم دستی موجودی چیپ"
    override val changeAmountLabel = "مقدار تغییر (مثبت یا منفی)"
    override val changeAmountHint = "برای کسر منفی بنویسید، مثل: -50"
    override val reasonLabel = "علت تغییر (اختیاری)"
    override val reasonPlaceholder = "مثلاً: اصلاح خطای دیلر"
    override fun chipsAfterAdjustment(amount: Long, currency: String) = "موجودی پس از تغییر: $amount $currency"
    override val applyAdjustment = "اعمال تغییر"

    override val settingsTitle = "تنظیمات بلایند و بازی"
    override val languageLabel = "زبان برنامه (Language)"
    override val languagePersian = "فارسی (Persian)"
    override val languageEnglish = "English (انگلیسی)"
    override val sbLabel = "Small Blind (SB)"
    override val bbLabel = "Big Blind (BB)"
    override val anteSettingLabel = "Ante (آنته)"
    override val autoPostBlindsLabel = "پست خودکار اسمال و بیگ بلایند"
    override val currencyNameLabel = "عنوان واحد امتیاز/چیپ"
    override val currencyNamePlaceholder = "مثلاً: چیپ، تومان، دلار"
    override val saveSettings = "ذخیره تنظیمات"
    override fun versionLabel(version: String) = "نسخه $version"

    override val templatesTitle = "تمپلیت‌های بازی (سیو چندگانه)"
    override val templatesSubtitle = "ذخیره و بازیابی ترکیب بازیکنان و وضعیت بازی"
    override val saveCurrentState = "ذخیره وضعیت فعلی به عنوان تمپلیت"
    override val saveCurrentAsTemplate = "ذخیره ترکیب فعلی به عنوان تمپلیت"
    override val templateNameLabel = "نام تمپلیت"
    override val templateNamePlaceholder = "مثلاً: دورهمی پنج‌شنبه‌ها"
    override val templateNameHint = "نامی برای ذخیره این ترکیب بازی وارد کنید"
    override val saveTemplateBtn = "ذخیره"
    override val savedTemplatesTitle = "تمپلیت‌های ذخیره‌شده"
    override val savedTemplatesHeader = "تمپلیت‌های از پیش ذخیره‌شده"
    override fun savedTemplatesHeader(count: Int) = "تمپلیت‌های از پیش ذخیره‌شده ($count)"
    override val noSavedTemplates = "هنوز تمپلیتی ذخیره نشده است."
    override val noTemplatesMessage = "هنوز هیچ تمپلیتی ذخیره نشده است. با زدن دکمه بالا وضعیت فعلی را ذخیره کنید."
    override val loadBtn = "بازیابی"
    override val restoreBtn = "بازیابی"
    override val updateBtn = "به‌روزرسانی"
    override val deleteBtn = "حذف"
    override val restoreTemplateTitle = "بازیابی تمپلیت"
    override fun restoreTemplateTitle(name: String) = "بازیابی تمپلیت: $name"
    override val restoreTemplateBody = "آیا مطمئن هستید که می‌خواهید این تمپلیت را بارگذاری کنید؟ وضعیت فعلی میز با این اطلاعات جایگزین خواهد شد."
    override fun restoreTemplateBody(players: Int, hands: Int) = "این تمپلیت شامل $players بازیکن است. با بازیابی آن، بازی فعلی ری‌استارت شده و وضعیت این تمپلیت جایگزین می‌شود."
    override val confirmRestoreBtn = "بله، بارگذاری شود"
    override val updateTemplateTitle = "به‌روزرسانی تمپلیت"
    override fun updateTemplateTitle(name: String) = "به‌روزرسانی تمپلیت: $name"
    override val updateTemplateBody = "آیا می‌خواهید این تمپلیت را با بازیکنان و تنظیمات فعلی بازنویسی کنید؟"
    override fun updateTemplateBody(name: String) = "آیا می‌خواهید تمپلیت «$name» را با بازیکنان، چیپ‌ها و تنظیمات فعلی میز بازنویسی کنید؟"
    override val confirmUpdateBtn = "بله، به‌روزرسانی شود"
    override val deleteTemplateTitle = "حذف تمپلیت"
    override fun deleteTemplateTitle(name: String) = "حذف تمپلیت: $name"
    override val deleteTemplateBody = "آیا از حذف دائمی این تمپلیت اطمینان دارید؟ این عملیات غیرقابل بازگشت است."
    override val confirmDeleteBtn = "حذف قطعی"
    override fun templatePlayerCount(count: Int) = "$count بازیکن"
    override fun templateTotalChips(amount: Long, currency: String) = "مجموع: $amount $currency"

    override val restartDialogTitle = "ری‌استارت بازی"
    override val restartConfirmQuestion = "آیا مطمئن هستید که می‌خواهید بازی را ری‌استارت کنید؟"
    override val restartWarningDetail = "با ری‌استارت، موجودی تمام بازیکنان به مقدار بای‌این اولیه بازمی‌گردد و شماره دست صفر خواهد شد."
    override val confirmRestartBtn = "بله، ری‌استارت کن"

    override val showdownTitle = "تعیین برنده و تقسیم پات"
    override val showdownSubtitle = "برنده یا برندگان هر پات را انتخاب کنید:"
    override val showdownSplitNote = "در صورت انتخاب چند بازیکن، پات به طور مساوی بین آنها تقسیم می‌شود."
    override val selectAtLeastOneWinner = "لطفاً برای هر پات حداقل یک برنده انتخاب کنید"
    override val selectWinnersWarning = "لطفاً برای تمام پات‌ها برنده مشخص کنید"
    override val confirmWinnersAndEndHand = "ثبت برندگان و اتمام دست"
    override val confirmAwardPots = "تقسیم چیپ‌ها و پایان دست"
    override val evenSplit = "تقسیم مساوی بین نفرات انتخاب‌شده"
    override fun evenSplit(share: Long, currency: String) = "سهم هر برنده: $share $currency (تقسیم مساوی)"

    override val analyticsTitle = "آمار و گزارش سود و زیان"
    override val analyticsSubtitle = "تحلیل عملکرد بازیکنان و تاریخچه دست‌ها"
    override val handsPlayedStat = "دست‌های بازی‌شده"
    override val handsPlayed = "دست‌های بازی شده"
    override val totalPotsStat = "مجموع پات‌ها"
    override val totalBuyInStat = "مجموع ورودی‌ها"
    override val totalBuyIns = "مجموع خریدها"
    override val leaderboardTitle = "جدول سود و زیان بازیکنان"
    override val topWinner = "بیشترین سود"
    override val noProfit = "بدون سود"
    override val playerCountLabel = "تعداد بازیکنان"
    override val initialBuyInStat = "ورودی:"
    override val currentChipsStat = "موجودی:"
    override val handsHistoryTitle = "تاریخچه دست‌های بازی"
    override val handLedgerTitle = "دفترچه ثبت مبالغ دست‌های بازی شده"
    override val noHandsHistory = "هنوز دستی به پایان نرسیده است."
    override val noHandsPlayedYet = "هنوز دستی به پایان نرسیده است. پس از پایان اولین دست، گزارش ورودی و خروجی دقیق هر شخص در اینجا ثبت می‌شود."
    override val winnersLabel = "برندگان:"
    override val potBreakdownLabel = "جزئیات پات‌ها:"
    override val playerChipChanges = "تغییر چیپ بازیکنان در این دست:"
    override val chartTitle = "نمودار سود و زیان بازیکنان"
    override val chartSubtitle = "محاسبه دقیق چیپ‌های فعلی منهای مبلغ خرید (Buy-in)"
    override val balanceExact = "تراز مالی میز کامل و دقیق است"
    override val balanceMismatch = "اختلاف در چیپ‌ها و ورودی‌ها"

    override val playerManagementTitle = "مدیریت بازیکنان"
    override val playersManagementTitle = "مدیریت بازیکنان و چیپ‌ها"
    override val playersManagementSubtitle = "افزودن، حذف، خرید مجدد و تغییر دستی چیپ"
    override val chipsInPlayStat = "کل چیپ‌های بازی"
    override val totalBuyInsStat = "مجموع خریدهای اولیه"
    override val chipBalanceStat = "وضعیت تراز چیپ‌ها"
    override val chipBalanceOk = "کاملاً تراز است ✓"
    override fun chipBalanceError(diff: Long, currency: String) = "عدم تراز: $diff $currency"
    override val addNewPlayerBtn = "افزودن بازیکن جدید"
    override val noPlayersYet = "هنوز بازیکنی به بازی اضافه نشده است."
    override val chipTransactionsTitle = "تراکنش‌های چیپ (بای‌این و تغییرات دستی)"
    override val buyInHistoryTitle = "تاریخچه ورودی‌ها و خریدهای مجدد (Buy-ins)"
    override val initialBuyIn = "ورودی اولیه"
    override val initialBuyInTag = "ورودی اولیه"
    override val rebuyTag = "خرید مجدد"
    override val rebuyHistory = "خرید مجدد"
    override val manualAdjustmentTag = "تغییر دستی"
    override val removePlayerConfirmTitle = "حذف بازیکن"
    override fun removePlayerConfirmMessage(name: String) = "آیا از حذف $name اطمینان دارید؟"
    override fun seatNumber(num: Int) = "صندلی شماره $num"

    override val initialLanguageTitle = "انتخاب زبان برنامه / Select Language"
    override val initialLanguageSubtitle = "لطفاً زبان مورد نظر خود را برای شروع انتخاب کنید:"
    override val persianOptionTitle = "فارسی (Persian)"
    override val persianOptionDesc = "تنظیم زبان به فارسی و واحد به «چیپ»"
    override val englishOptionTitle = "English"
    override val englishOptionDesc = "Set language to English and unit to \"chip\""
    override val confirmLanguageBtn = "تأیید و شروع بازی"

    override val raiseInputLabel = "تعداد چیپ شرط"
    override fun stepSbInfo(amount: Long, currency: String) = "گام تغییر: ±$amount $currency (اسمال بلایند)"
}

object EnglishStrings : AppStrings {
    override val languageCode = "en"

    override val navTable = "Table"
    override val navAnalytics = "Analytics"
    override val navPlayers = "Players"

    override val handNumber = "Hand"
    override fun handNumber(num: Int) = "Hand #$num"
    override val totalPot = "Total Pot"
    override val totalPotLabel = "Total Pot"
    override fun blindsLabel(sb: Long, bb: Long) = "Blinds: $sb / $bb"
    override fun blindsLabel(sb: Long, bb: Long, currency: String) = "Blinds: $sb / $bb $currency"
    override fun anteLabel(ante: Long) = "Ante: $ante"
    override val currentTurn = "Turn:"
    override val startFirstHand = "Start First Hand"
    override val startNewHand = "Start New Hand"
    override val restart = "Restart"
    override val restartBtn = "Restart"
    override val restartGameTooltip = "Restart Game"
    override val templatesTooltip = "Game Templates"
    override val addPlayerTooltip = "Add Player"
    override val settingsTooltip = "Game Settings"
    override val languageToggleTooltip = "Toggle Language"
    override val playersWithChips = "Players with chips"

    override val mainPot = "Main Pot"
    override fun sidePot(index: Int) = "Side Pot $index"
    override val eligiblePlayers = "Eligible:"
    override val activeBetsOnTable = "Active bets on table:"
    override val inPot = "in pot"
    override val noActivePots = "No active pots yet"
    override val totalTablePot = "Total Pots on Table"

    override val dealerBadge = "D"
    override val smallBlindBadge = "SB"
    override val bigBlindBadge = "BB"
    override val chipsLabel = "Chips"
    override val buyInLabel = "Buy-in"
    override val totalBuyInLabel = "Total Buy-in"
    override val netProfitLossLabel = "Net P/L"
    override val netPnlLabel = "Net P/L"
    override val pnlLabel = "Net P/L"
    override val rebuyBtn = "Rebuy"
    override val adjustBtn = "Adjust"
    override val removeBtn = "Remove"
    override val removePlayerLabel = "Remove Player"
    override val playerTurnIndicator = "Current Turn"
    override val betThisHandLabel = "Bet this hand"
    override val turnBanner = "This player's turn"

    override val turnPlayer = "Player's Turn"
    override fun turnPlayer(name: String) = "Turn: $name"
    override fun playerTurnTitle(name: String) = "$name's Turn"
    override fun highestBetInfo(amount: Long, currency: String) = "Highest Bet: $amount $currency"
    override fun potInfo(amount: Long, currency: String) = "Pot: $amount $currency"
    override fun minRaiseInfo(amount: Long, currency: String) = "Min Raise: $amount $currency"
    override val fold = "Fold"
    override val foldBtn = "Fold"
    override val check = "Check"
    override val checkBtn = "Check"
    override val call = "Call"
    override fun callBtn(amount: Long, currency: String) = "Call $amount $currency"
    override val bet = "Bet"
    override fun betBtn(amount: Long, currency: String) = "Bet $amount $currency"
    override val raise = "Raise"
    override fun raiseToBtn(amount: Long, currency: String) = "Raise to $amount $currency"
    override val allIn = "All-In"
    override fun allInBtn(amount: Long, currency: String) = "All-In ($amount $currency)"
    override val presetMin = "Min"
    override val presetHalfPot = "½ Pot"
    override val presetPot = "Pot"
    override val presetAllIn = "All-In"
    override val betAmountLabel = "Bet Amount"
    override val raiseAmountLabel = "Total Raise Amount"
    override val quickMin = "Min"
    override val quick2x = "2X BB"
    override val quickHalfPot = "½ Pot"
    override val quickPot = "Pot"
    override val quickAllIn = "All-In"
    override val confirmRaise = "Confirm Raise"

    override val addPlayerTitle = "Add New Player"
    override val playerNameLabel = "Player Name"
    override val playerNamePlaceholder = "e.g., Alex"
    override val buyInInputLabel = "Initial Buy-in / Chips"
    override val avatarColorLabel = "Player Avatar Color:"
    override val addPlayerConfirm = "Add Player"
    override val cancel = "Cancel"

    override val rebuyTitle = "Rebuy (Add Chips)"
    override fun rebuyForPlayer(name: String) = "Player: $name"
    override val currentChips = "Current Chips"
    override fun currentChips(amount: Long, currency: String) = "Current Chips: $amount $currency"
    override val rebuyAmountLabel = "Rebuy Amount"
    override val confirmRebuy = "Confirm Rebuy"

    override val adjustChipsTitle = "Manual Chip Adjustment"
    override val changeAmountLabel = "Change Amount (+ or -)"
    override val changeAmountHint = "Use minus for deduction, e.g. -50"
    override val reasonLabel = "Reason (Optional)"
    override val reasonPlaceholder = "e.g., Dealer error correction"
    override fun chipsAfterAdjustment(amount: Long, currency: String) = "Chips after change: $amount $currency"
    override val applyAdjustment = "Apply Change"

    override val settingsTitle = "Game & Blind Settings"
    override val languageLabel = "Application Language"
    override val languagePersian = "Persian (فارسی)"
    override val languageEnglish = "English"
    override val sbLabel = "Small Blind (SB)"
    override val bbLabel = "Big Blind (BB)"
    override val anteSettingLabel = "Ante"
    override val autoPostBlindsLabel = "Auto-post Small & Big Blinds"
    override val currencyNameLabel = "Chip/Currency Unit Name"
    override val currencyNamePlaceholder = "e.g., Chips, USD, EUR"
    override val saveSettings = "Save Settings"
    override fun versionLabel(version: String) = "Version $version"

    override val templatesTitle = "Game Templates (Multi-Save)"
    override val templatesSubtitle = "Save and restore player groups and game states"
    override val saveCurrentState = "Save Current Game as Template"
    override val saveCurrentAsTemplate = "Save Current Game as Template"
    override val templateNameLabel = "Template Name"
    override val templateNamePlaceholder = "e.g., Friday Night Crew"
    override val templateNameHint = "Enter a name for this game template"
    override val saveTemplateBtn = "Save"
    override val savedTemplatesTitle = "Saved Templates"
    override val savedTemplatesHeader = "Saved Game Templates"
    override fun savedTemplatesHeader(count: Int) = "Saved Game Templates ($count)"
    override val noSavedTemplates = "No templates saved yet."
    override val noTemplatesMessage = "No templates saved yet. Tap the button above to save the current table."
    override val loadBtn = "Load"
    override val restoreBtn = "Restore"
    override val updateBtn = "Update"
    override val deleteBtn = "Delete"
    override val restoreTemplateTitle = "Restore Template"
    override fun restoreTemplateTitle(name: String) = "Restore Template: $name"
    override val restoreTemplateBody = "Are you sure you want to load this template? The current table state will be replaced."
    override fun restoreTemplateBody(players: Int, hands: Int) = "This template has $players players. Restoring it will restart the game and load this template's players and settings."
    override val confirmRestoreBtn = "Yes, Load Template"
    override val updateTemplateTitle = "Update Template"
    override fun updateTemplateTitle(name: String) = "Update Template: $name"
    override val updateTemplateBody = "Do you want to overwrite this template with the current table players and settings?"
    override fun updateTemplateBody(name: String) = "Do you want to overwrite template \"$name\" with current table players and settings?"
    override val confirmUpdateBtn = "Yes, Update Template"
    override val deleteTemplateTitle = "Delete Template"
    override fun deleteTemplateTitle(name: String) = "Delete Template: $name"
    override val deleteTemplateBody = "Are you sure you want to permanently delete this template? This cannot be undone."
    override val confirmDeleteBtn = "Delete Permanently"
    override fun templatePlayerCount(count: Int) = "$count players"
    override fun templateTotalChips(amount: Long, currency: String) = "Total: $amount $currency"

    override val restartDialogTitle = "Restart Game"
    override val restartConfirmQuestion = "Are you sure you want to restart the game?"
    override val restartWarningDetail = "Restarting will reset all players' chip counts to their initial buy-ins and clear the hand counter."
    override val confirmRestartBtn = "Yes, Restart"

    override val showdownTitle = "Showdown & Pot Distribution"
    override val showdownSubtitle = "Select the winner(s) for each pot:"
    override val showdownSplitNote = "If multiple players are selected, the pot will be split equally among them."
    override val selectAtLeastOneWinner = "Please select at least one winner for each pot"
    override val selectWinnersWarning = "Please select winner(s) for all active pots"
    override val confirmWinnersAndEndHand = "Confirm Winners & End Hand"
    override val confirmAwardPots = "Award Chips & End Hand"
    override val evenSplit = "Even split between selected winners"
    override fun evenSplit(share: Long, currency: String) = "Each winner receives: $share $currency (Even Split)"

    override val analyticsTitle = "Analytics & Profit/Loss"
    override val analyticsSubtitle = "Player performance analytics and hand history ledger"
    override val handsPlayedStat = "Hands Played"
    override val handsPlayed = "Hands Played"
    override val totalPotsStat = "Total Pots"
    override val totalBuyInStat = "Total Buy-ins"
    override val totalBuyIns = "Total Buy-ins"
    override val leaderboardTitle = "Player Profit & Loss Leaderboard"
    override val topWinner = "Top Winner"
    override val noProfit = "No profit"
    override val playerCountLabel = "Player Count"
    override val initialBuyInStat = "Buy-in:"
    override val currentChipsStat = "Chips:"
    override val handsHistoryTitle = "Hand History"
    override val handLedgerTitle = "Completed Hands History Ledger"
    override val noHandsHistory = "No completed hands yet."
    override val noHandsPlayedYet = "No hands completed yet. After the first hand finishes, exact chip deltas and results will appear here."
    override val winnersLabel = "Winners:"
    override val potBreakdownLabel = "Pots breakdown:"
    override val playerChipChanges = "Player chip changes in this hand:"
    override val chartTitle = "Player Profit & Loss Chart"
    override val chartSubtitle = "Current chip stack minus total buy-in"
    override val balanceExact = "Table ledger is fully balanced"
    override val balanceMismatch = "Discrepancy in chip totals"

    override val playerManagementTitle = "Player Management"
    override val playersManagementTitle = "Players & Chip Management"
    override val playersManagementSubtitle = "Add, remove, rebuy, and manually adjust chips"
    override val chipsInPlayStat = "Total Chips in Play"
    override val totalBuyInsStat = "Total Buy-ins"
    override val chipBalanceStat = "Chip Ledger Integrity"
    override val chipBalanceOk = "Balanced ✓"
    override fun chipBalanceError(diff: Long, currency: String) = "Imbalance: $diff $currency"
    override val addNewPlayerBtn = "Add New Player"
    override val noPlayersYet = "No players added yet."
    override val chipTransactionsTitle = "Chip Transactions (Buy-ins & Adjustments)"
    override val buyInHistoryTitle = "Buy-in & Rebuy History"
    override val initialBuyIn = "Initial Buy-in"
    override val initialBuyInTag = "Initial Buy-in"
    override val rebuyTag = "Rebuy"
    override val rebuyHistory = "Rebuy"
    override val manualAdjustmentTag = "Manual Adjustment"
    override val removePlayerConfirmTitle = "Remove Player"
    override fun removePlayerConfirmMessage(name: String) = "Are you sure you want to remove $name?"
    override fun seatNumber(num: Int) = "Seat #$num"

    override val initialLanguageTitle = "Select Language / انتخاب زبان"
    override val initialLanguageSubtitle = "Please select your preferred language to get started:"
    override val persianOptionTitle = "فارسی (Persian)"
    override val persianOptionDesc = "تنظیم زبان به فارسی و واحد به «چیپ»"
    override val englishOptionTitle = "English"
    override val englishOptionDesc = "Set language to English and chip unit to \"chip\""
    override val confirmLanguageBtn = "Confirm & Start Game"

    override val raiseInputLabel = "Bet/Raise Chip Amount"
    override fun stepSbInfo(amount: Long, currency: String) = "Step: ±$amount $currency (Small Blind)"
}

fun getAppStrings(lang: String): AppStrings {
    return if (lang.equals("en", ignoreCase = true)) EnglishStrings else PersianStrings
}

val LocalAppStrings = staticCompositionLocalOf<AppStrings> { PersianStrings }
