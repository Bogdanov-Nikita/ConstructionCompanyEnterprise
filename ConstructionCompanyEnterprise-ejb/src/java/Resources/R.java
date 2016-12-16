/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Resources;

/**
 *
 * @author Nik
 */
//некоторые из значений могут быть заменены в дальнейшем.
//хранит общие идентификаторы и значения и enum для разных случаев, аналог в Android
public final class R{
    public final static String DataFormat = "dd.MM.yyyy HH:mm:ss";
    
    public final static String ErrorDialogTitle = "Ошибка";
    
    public static final class FileName{
    }
    public static final class ErrMsg{
        
        public final static String ERROR = "error";
        
        //All error massage
        public final static String BEAN_CONNECTION_ERROR = "Невозможно подлючится к сервису.";
        public final static String UNSUPPORT_EXEPTION = "Данная роль не поддерживает такую операцию";
        public final static String ARRAY_ERROR = "Данного массива данных не существует";
        public final static String RECORD_NOT_FOUND = "Данной записи не существует";
        
        public final static String NAME_ERROR = "Не заполненно поле Ф.И.О.!";
        public final static String PHONE_ERROR = "Не заполненно поле телефонного номера!";
        public final static String ADDRESS_ERROR = "Не заполненно поле адреса!";
        public final static String TYPE_ERROR = "Не указан тип клиента";
        public final static String ADD_ERROR = "Невозможно создать запись";
        public final static String EDIT_ERROR = "Невозможно изменить запись";
        public final static String SELECT_ERROR = "Невозможно получить запись";
        public final static String DELETE_ERROR = "Невозможно удалить";
        public final static String NUMBER_ERROR = "Не указан номер заказа";
        public final static String INPUT_DATA_ERROR_ID = "Неверно указан идентфикатор";
        public final static String INPUT_DATA_ERROR_1 = "Данных нет или они не корректны";
        public final static String INPUT_DATA_ERROR_2 = "Введённое значение не корректно";
        public final static String INPUT_DATA_ERROR_3 = "Ведённое число не должно быть отрицательно";
        public final static String INPUT_PAY_ERROR = "Невозможно выполнить оплату";
        public final static String TAKE_WORK_ERROR = "Невозможно принять данную работу";
        public final static String NOT_CORRECT_ID_OR_ACSESS_ERROR = "Нет доступа к записи или неверный идентификатор";
        public final static String INPUT_RESOURCE_NAME_ERROR = "Не заполненно поле название ресурса";
        public final static String INPUT_RESOURCE_COAST_ERROR_1 = "Не заполненно поле стоимости ресурса";
        public final static String INPUT_RESOURCE_COAST_ERROR_2 = "Стоимость ресурса не может быть отрицательна";
        public final static String INPUT_RESOURCE_TYPE_ERROR_1 = "Не заполненно поле кода ресурса";
        public final static String INPUT_RESOURCE_TYPE_ERROR_2 = "Код ресурса не может быть отрицательным числом";
        public final static String INPUT_STORAGE_ERROR = "Не заполненно поле адреса склада";
        public final static String INPUT_WORK_DESCRIPTION_ERROR = "Не заполненно поле описание работы";
        public final static String INPUT_WORK_COAST_ERROR_1 = "Не заполненно поле стоимости работы";
        public final static String INPUT_WORK_COAST_ERROR_2 = "Стоимость работы не может быть отрицательна";
        public final static String INPUT_AMOUNT_ERROR_1 = "Не заполненно поле количества";
        public final static String INPUT_AMOUNT_ERROR_2 = "Количество ресурсов неможет быть отрицательннм";
        
        public final static String StorageAmountError = "Нельзя взять больше чем есть есть на складе";
    }
    
    public static final class InfoMsg{
        public final static String INFO = "info";
        public final static String SUCSESS = "sucsess";
    }
    
    public static final class RoleType{
        public final static String Defaul = "Default";        
        public final static String ConfigManager = "Manager";
        public final static String ConfigClient  = "Client";
        public final static String ConfigMaster  = "Master";
        public final static String Manager = "Менеджер";
        public final static String Client  = "Заказчик";
        public final static String Master  = "Прораб";
        
    }
    
    public final static class ArraysName{
        public final static String ORDER = "orders";
        public final static String STORAGE = "storages";
        public final static String ESTIMATE_WORK = "estimateworks";
        public final static String WORK = "works";
        public final static String RESOURCE_ITEM = "resourceitems";
        public final static String RESOURCE = "resources";
        public final static String ESTIMATE = "estimates";
        public final static String MANAGER = "managers";
        public final static String CLIENT = "clients";
        public final static String MASTER = "masters";
    }

    public static final class ModelType {
        public final static int DefaultModel = 0x0;
        public final static int ManagerModel = 0x1;
        public final static int MasterModel  = 0x2;
        public final static int ClientModel  = 0x3;        
    }
    public static final class ModelEvent {
        public final static int DefaultEvent  = 0x0;
        public final static int onClickOk     = 0x1;
        public final static int onClickCansel = 0x2;
        public final static int onClickAction = 0x3;
    }
    public static final class Mapper{
        public final static int loadOrderListbyManager = 0x1;
        public final static int loadManagerWork = 0x2;
        public final static int loadManagerResourceList = 0x3;
        public final static int loadManagerStorageList = 0x4;
        public final static int loadManagerClientList = 0x5;
        public final static int loadManagerMasterList = 0x6;
        public final static int loadClientOrderList = 0x7;
        public final static int loadMasterEstimateList = 0x8;
    }
    public static final class Client{
        public final static String CLIENT_TYPE = "Тип:";
        public final static String DEFAULT = "неизвстен";
        public final static String PHYSICAL = "физический";
        public final static String LEGAL = "юридический";
        
        public static String ClientTypeName(int type){
            String str = DEFAULT;
            switch(type){
                case entity.Client.PHYSICAL: str = PHYSICAL; break;
                case entity.Client.LEGAL: str = LEGAL; break;
            }
            return str;
        }
        
        public static int ClientNameType(String name){
            int type = 0;
            switch(name){
                case PHYSICAL: type = entity.Client.PHYSICAL; break;
                case LEGAL: type = entity.Client.LEGAL; break;
            }
            return type;
        }
        
    }
    public static final class Order{
        public final static String DEFAULT = "Неизвстен";
        public final static String OPEN = "открыт";
        public final static String INPROGRESS = "отправлен на исполнение";
        public final static String WAITING_ACKNOWLEDGMENT_TAKE = "отправлен на подтверждение, клиенту";
        public final static String WAITING_PAY = "ожидание оплаты от клиента";
        public final static String WAITING_ACKNOWLEDGMENT_PAY = "на подтверждение оплаты, менеджеру";
        public final static String CLOSE = "закрыт";
        
        public static String StatusName(int Status){
            String str = DEFAULT;
            switch(Status){
                case entity.ConstructionOrder.OPEN: str = OPEN; break;
                case entity.ConstructionOrder.INPROGRESS: str = INPROGRESS; break;
                case entity.ConstructionOrder.WAITING_ACKNOWLEDGMENT_TAKE: str = WAITING_ACKNOWLEDGMENT_TAKE; break;
                case entity.ConstructionOrder.WAITING_PAY: str = WAITING_PAY; break;
                case entity.ConstructionOrder.WAITING_ACKNOWLEDGMENT_PAY: str = WAITING_ACKNOWLEDGMENT_PAY; break;
                case entity.ConstructionOrder.CLOSE: str = CLOSE; break;  
            }
            return str;
        }        
    }
    
    public static final class Estimate{
        public final static String DEFAULT = "неизвстен";
        public final static String PAID = "оплачена";
        public final static String FINISH = "завершена";
        public final static String NOTPAID = "не оплачена";
        public final static String NOTFINISH = "не завершена";
        public final static String MAIN = "основная";
        public final static String ADDITIONAL = "дополнительная";
        
        public static String TypeName(int type){
            String str = DEFAULT;
            switch(type){
                case entity.Estimate.MAIN: str = MAIN; break;
                case entity.Estimate.ADDITIONAL: str =ADDITIONAL; break;
            }
            return str;
        }
        
        public static String StatusName(boolean isPaid,boolean isFinish){
            return ((isPaid)? (PAID) : (NOTPAID)) + " | " + ((isFinish) ? (FINISH) : (NOTFINISH));
        }
    }
    public static final class Work{        
        public final static String FINISH = "завершена";
        public final static String NOTFINISH = "не завершена";
        
        public static String StatusName(boolean isFinish){         
            return (isFinish) ? (FINISH) : (NOTFINISH);
        }
    }
    public static final class Dialog{
        //Dialog Action
        public final static int AddAction = 1;
        public final static int EditAction = 2;
        public final static int SendAction = 3;
        public final static int TakeAction = 4;
        //Title
        public final static String Send = "Отправить ресурс";
        public final static String Take = "Взять ресурс";
        public final static String Add = "Добавить";
        public final static String Edit = "Редактировать";
        public final static String Delete = "Удалить";
        public final static String Update = "Обновление";
        //View
        public final static String Order = "Заказ:";
        public final static String Client = "Клиент:";
        public final static String Manager = "Менеджер:";
        public final static String Status = "Статус:";
        public final static String TotalCoast = "Общая стоимость:";
        public final static String CurrentCoast = "Текущая стоимость:";
        public final static String Create  = "Дата создания:";
        public final static String LastUpdate = "Дата последнего обновления:";
        public final static String End = "Дата завершения:";
        //Input
        public final static String ClientPayInputMsg = "Введите сколько хотите заплатить";
        public final static String ClienPayAccept = "Оплата проведена успешно";
        //Info        
        public final static String UpdateMsg = "Нет данных для обновления";
    }
}
