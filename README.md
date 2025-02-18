# 🚗 Araç Görev ve Bakım Takip Uygulaması

Bu proje, **Yaşar Bilgi Teknolojileri** staj sürecinde geliştirilen bir **mobil uygulamadır**. Uygulama, **araç bakım ve görev süreçlerini kayıt altına almayı, yönetmeyi ve dışa aktarmayı** amaçlamaktadır. Kullanıcılar, araçların bakım durumlarını takip edebilir, geçmiş bakım kayıtlarına erişebilir ve bunları dışa aktararak farklı formatlarda paylaşabilir.  


## 🛠 Kullanılan Teknolojiler ve Araçlar  

- **Programlama Dili:** Java  
- **Geliştirme Ortamı:** Android Studio  
- **Veritabanı:** Firebase, SQLite  
- **Dosya İşlemleri:** PDF, Excel, E-posta ile paylaşım  

## 📌 Özellikler  

### 🚘 **1. Plaka Bazlı Kayıt Sistemi**  
- Kullanıcılar, **araç plakası** üzerinden bakım ve görev kayıtlarını oluşturabilir.  
- Plaka kaydı oluşturulduktan sonra, detayları görüntülemek veya düzenlemek mümkündür.  
- **Silme ve düzenleme işlemleri** Firebase ile senkronize çalışır.  

### 📊 **2. Detaylı Araç Bakım Kaydı**  
- Her araç için, **bakım geçmişi ve görev detayları** saklanır.  
- Araç ile ilgili şu bilgiler kayıt altına alınabilir:  
  - **Araç tipi**   
  - **Görev durumu** 
  - **Başlangıç kilometresi ve dönüş kilometresi**  
  - **Araç bakım zamanı ve türü**  
  - **Özel notlar ve ek bilgiler**
    
### 🔍 **3. Gelişmiş Filtreleme Sistemi**  
- Kullanıcılar, **araç türü** ve **aktif/pasif durumu** bazında listeleme yapabilir.  
- Listeleme seçenekleri:  
  - **Tüm araçları göster**  
  - **Sadece aktif araçlar**  
  - **Sadece pasif araçlar**  
  - **Özel araçları göster (ikon ile belirtme)**  

### 📂 **4. Dosya Dışa Aktarma ve Paylaşım**  
- **PDF Dışa Aktarma:** Araç görev raporları, PDF formatında oluşturulup kaydedilebilir.  
- **Excel Dışa Aktarma:** Araç bakım bilgileri Excel dosyası olarak dışa aktarılabilir.  
- **E-posta ile Paylaşma:** Kullanıcı, oluşturduğu PDF veya Excel dosyasını direkt e-posta ile gönderebilir.  

### 🎨 **5. Modern ve Kullanıcı Dostu Arayüz**  
- **Material Design** prensiplerine uygun tasarlanmış UI.  
- **Liste Görünümü (ListView ve CardView desteği)** ile araçları listeleme.  
- **Tema ve ikon desteği:** Özel araçlar için farklı ikon kullanımı.  

### ⚙ **6. Kullanıcı Ayarları ve Kişiselleştirme**  
- **Tercihler Kaydedilir:** Kullanıcı, uygulamayı kapatıp açtığında, son seçilen filtreler ve ayarlar korunur.  


