suppressPackageStartupMessages({
    library(dplyr)
    library(readr)
})
options(pillar.width = Inf, pillar.sigfig = 12)

print_log <- function(prompt, value){
    cat(sprintf(prompt, value))
}

load_data <- function(path) {
    df <- read_csv(path, 
                    col_types = cols(.default = col_character()),
                    show_col_types = FALSE)
    print_log("Total Rows Loaded: %d\n", nrow(df))

    return (df)
}

clean_data <- function(df){
    df  <- df %>%
        mutate(currency = toupper(trimws(currency)), type = tolower(trimws(type)),
                account_name = trimws(account_name)) %>%
        filter(currency == "PHP")
    
    print_log("Rows after filtering to PHP: %d\n", nrow(df))

    df <- df %>%
        mutate(date = as.Date(trimws(date), format = "%d/%m/%Y"),
                amount = suppressWarnings(as.numeric(gsub(",", "", trimws(amount)))),
                current_balance = suppressWarnings(as.numeric(gsub(",", "", trimws(current_balance))))
                )

    invalid_dates <- sum(is.na(df$date))
    invalid_amt <- sum(is.na(df$amount))
    invalid_curr_bal <- sum(is.na(df$current_balance))
    invalid_type <- sum(!(df$type %in% c("deposit", "withdrawal")))

    print_log("Total rows with invalid date: %d\n", invalid_dates)
    print_log("Total rows with invalid amount: %d\n", invalid_amt)
    print_log("Total rows with invalid current balance: %d\n", invalid_curr_bal)
    print_log("Total rows with invalid type: %d\n", invalid_type)

    df <- df %>% 
        filter(!is.na(account_name), !is.na(account_number), !is.na(date), !is.na(amount),
                !is.na(current_balance), type %in% c("deposit", "withdrawal"))

    print_log("Total rows after cleaning: %d\n", nrow(df))

    return (df)
}

get_daily_balances <- function(df){
    # Gets the date for the first transaction per account
    data_frame <- df %>%
        group_by(account_number) %>%
        summarize(date_of_first_transaction = min(date))

    # Gets the last transaction per account per day and sets that as the end of the day balance
    eod_df <- df %>%
        group_by(account_number, date) %>%
        slice_tail(n = 1) %>%
        ungroup() %>%
        select(account_name, account_number, date, end_of_day_balance = current_balance)
    
    # Generates a table of dates starting from the earliest transaction upto the latest, incrementing it by day
    days <- tibble(date = seq(min(df$date), max(df$date), by = "day"))

    # Combines the Tables
    data_frame <- data_frame %>% 
        cross_join(days) %>%
        filter(date >= date_of_first_transaction) %>%
        left_join(eod_df, by = c("account_number", "date")) %>%
        arrange(account_number, date)
    
    # Fills in the balances of days without transaction
    data_frame <- data_frame %>%
        group_by(account_number) %>%
        mutate(last_row = cummax(ifelse(is.na(end_of_day_balance), 0, row_number())), end_of_day_balance = end_of_day_balance[last_row], account_name = account_name[last_row]) %>%
        ungroup() %>%
        select(account_name, account_number, date, end_of_day_balance)

    return (data_frame)
}

get_privilege_tiers <- function(df){
    latest_date <- max(df$date)
    start_this_month <- as.Date(format(latest_date, "%Y-%m-01"))
    start_last_month <- seq(start_this_month, by = "-1 month", length.out = 2)[2]
    end_last_month <- start_this_month - 1
    days_in_month <- as.numeric(end_last_month - start_last_month) + 1

    data_frame <- df %>%
        group_by(account_name, account_number) %>%
        summarize(first_day = min(date), current_balance = last(end_of_day_balance),
                    AverageDailyBalance = sum(end_of_day_balance[date >= start_last_month & date <= end_last_month]) / days_in_month,
                    .groups = "drop") %>%
        filter(AverageDailyBalance >= 100000.00) %>%
        mutate(AccountName = account_name, CurrentBalance = current_balance,
                AccountAge = as.numeric(latest_date - first_day),
                PrivilegeTier = case_when(AverageDailyBalance < 500000.00 ~ "Silver", AverageDailyBalance < 1000000.00 ~ "Gold", .default = "Platinum")) %>%
        arrange(desc(AverageDailyBalance)) %>%
        select(AccountName, AccountAge, CurrentBalance, AverageDailyBalance, PrivilegeTier)

    return (data_frame)
}

get_interest_report <- function(df){

    data_frame <- df %>%
        group_by(account_number) %>%
        slice_tail(n = 1) %>%
        ungroup() %>%

        filter(current_balance >= 10000.00) %>%
        mutate(ExpectedInterest = current_balance * ((1 + 0.05/365) ^ 30 - 1)) %>%
        arrange(desc(ExpectedInterest)) %>%
        mutate(AccountName = account_name, CurrentBalance = current_balance) %>%
        select(AccountName, CurrentBalance, ExpectedInterest)
    
    return (data_frame)
}

get_suspicious_report <- function(df){
    data_frame <- df %>%
        group_by(account_name, account_number, date) %>%
        summarize(TotalWithdrawalAmounts = sum(amount[type == "withdrawal"]), 
                    NumberOfDeposits = sum(type == "deposit"), 
                    TotalDepositAmounts = (sum(amount[type == "deposit"])),
                    .groups = "drop") %>%
        mutate(TurnoverVelocity = ifelse(TotalDepositAmounts == 0, 0, (TotalWithdrawalAmounts / TotalDepositAmounts) * 100), 
                RiskFlagLevel = ifelse(TurnoverVelocity >= 90 | TotalWithdrawalAmounts >= 500000.00, "Suspicious", "Normal")) %>%
        arrange(date, desc(RiskFlagLevel), account_name) %>%
        mutate(AccountName = account_name, Date = date) %>%
        select(AccountName, Date, TotalWithdrawalAmounts, NumberOfDeposits, TotalDepositAmounts, TurnoverVelocity, RiskFlagLevel)
    
    return (data_frame)
}

get_statistical_report <- function(df){
    data_frame <- df %>%
        group_by(account_name, account_number) %>%
        summarize(MeanBalance = mean(end_of_day_balance), MedianBalance = median(end_of_day_balance),
                    AccountStandardDev = ifelse(n() > 1, sd(end_of_day_balance), 0),
                    .groups = "drop") %>%
        mutate(StabilityLevel = ifelse(MeanBalance == 0, 0, (AccountStandardDev / MeanBalance) * 100),
                StabilityProfile = case_when(StabilityLevel >= 75 ~ "Unstable", StabilityLevel >= 25 ~ "Moderate", .default = "Stable")) %>%
        arrange(desc(StabilityLevel), account_number) %>%
        select(AccountName = account_name, AccountNumber = account_number, MedianBalance, AccountStandardDev, StabilityLevel, StabilityProfile)
    
    return (data_frame)
}

format_numeric <- function(value){
    return (sprintf("%.2f", value))
}


# ----------- MAIN LOGIC ----------
data_frame <- clean_data(load_data("bank_transactions.csv"))

# cat("---------- REPORT 1 ------------\n")

daily_balances <- get_daily_balances(data_frame)
# priv_tiers <- get_privilege_tiers(daily_balances)

# report1 <- priv_tiers %>%
#     mutate(CurrentBalance = format_numeric(CurrentBalance), AverageDailyBalance = format_numeric(AverageDailyBalance))

# write_csv(report1, "R_report1_banking_privilege_tiers.csv")

# cat("---------- REPORT 2 ------------\n")
# interest_report <- get_interest_report(data_frame)

# report2 <- interest_report %>%
#     mutate(CurrentBalance = format_numeric(CurrentBalance), ExpectedInterest = format_numeric(ExpectedInterest))

# write_csv(report2, "R_report2_interest_expense_projection.csv")

# cat("---------- REPORT 3 ------------\n")
# suspicious_report <- get_suspicious_report(data_frame)

# report3 <- suspicious_report %>%
#     mutate(TotalWithdrawalAmounts = format_numeric(TotalWithdrawalAmounts), TotalDepositAmounts = format_numeric(TotalDepositAmounts),
#             TurnoverVelocity = format_numeric(TurnoverVelocity))

# write_csv(report3, "R_report3_suspicious_client_behavior.csv")

# cat("---------- REPORT 4 ------------\n")

stats_report <- get_statistical_report(daily_balances)
report4 <- stats_report %>%
    mutate(MedianBalance = format_numeric(MedianBalance), AccountStandardDev = format_numeric(AccountStandardDev), 
            StabilityLevel = format_numeric(StabilityLevel))

write_csv(report4, "report4_stats_report_on_account.csv")