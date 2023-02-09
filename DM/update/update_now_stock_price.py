import main
import datetime
if __name__ == '__main__':
    nowTime = datetime.datetime.now()
    print(nowTime)
    print("start update_now_stock_price")
    update = main.Update()
    update.update_now_price()