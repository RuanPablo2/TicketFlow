package com.RuanPablo2.TicketFlow.config;

import com.RuanPablo2.TicketFlow.entity.Message;
import com.RuanPablo2.TicketFlow.entity.Ticket;
import com.RuanPablo2.TicketFlow.entity.User;
import com.RuanPablo2.TicketFlow.entity.enums.Role;
import com.RuanPablo2.TicketFlow.entity.enums.TicketCategory;
import com.RuanPablo2.TicketFlow.entity.enums.TicketPriority;
import com.RuanPablo2.TicketFlow.entity.enums.TicketStatus;
import com.RuanPablo2.TicketFlow.repository.MessageRepository;
import com.RuanPablo2.TicketFlow.repository.TicketRepository;
import com.RuanPablo2.TicketFlow.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class DemoSeeder {

    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final MessageRepository messageRepository;
    private final PasswordEncoder passwordEncoder;

    public DemoSeeder(UserRepository userRepository, TicketRepository ticketRepository,
                      MessageRepository messageRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
        this.messageRepository = messageRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void seed() {
        if (userRepository.count() > 0) {
            return;
        }

        System.out.println("🌱 [DEMO SEEDER] Populando ambiente de demonstração com atores e chamados...");

        String defaultPassword = passwordEncoder.encode("123456");

        User admin = new User();
        admin.setEmail("admin@ticketflow.com");
        admin.setName("Admin Master");
        admin.setPassword(defaultPassword);
        admin.setRole(Role.ADMIN);

        User client = new User();
        client.setEmail("client-test@ticketflow.com");
        client.setName("Client test");
        client.setPassword(defaultPassword);
        client.setRole(Role.CLIENT);

        User support = new User();
        support.setEmail("support-test@ticketflow.com");
        support.setName("João suporte");
        support.setPassword(defaultPassword);
        support.setRole(Role.SUPPORT);

        userRepository.saveAll(List.of(admin, client, support));

        Ticket tQuestion = new Ticket();
        tQuestion.setCategory(TicketCategory.QUESTION);
        tQuestion.setPriority(TicketPriority.LOW);
        tQuestion.setStatus(TicketStatus.OPEN);
        tQuestion.setTitle("Como faço para cancelar minha assinatura?");
        tQuestion.setDescription("Pessoal, não vou mais precisar usar o sistema no mês que vem. Onde fica a opção no menu para eu cancelar a renovação automática da minha conta? Procurei nas configurações e não achei.");
        tQuestion.setClient(client);
        tQuestion.setCreatedAt(LocalDateTime.now().minusHours(1));

        Ticket tBug = new Ticket();
        tBug.setCategory(TicketCategory.BUG);
        tBug.setPriority(TicketPriority.URGENT);
        tBug.setStatus(TicketStatus.IN_PROGRESS);
        tBug.setTitle("Botão de \"Salvar\" não está funcionando no meu perfil");
        tBug.setDescription("Olá. Toda vez que eu tento atualizar meu endereço no perfil e clico em \"Salvar\", a tela fica carregando infinitamente e a alteração não é feita. Já tentei pelo celular e pelo computador, mas o erro continua. Podem verificar?");
        tBug.setClient(client);
        tBug.setAssignedSupport(support);
        tBug.setCreatedAt(LocalDateTime.now().minusHours(3));

        Ticket tAccess = new Ticket();
        tAccess.setCategory(TicketCategory.ACCESS);
        tAccess.setPriority(TicketPriority.MEDIUM);
        tAccess.setStatus(TicketStatus.WAITING_CUSTOMER);
        tAccess.setTitle("E-mail de redefinição de senha não chega");
        tAccess.setDescription("Bom dia, estou tentando acessar minha conta mas não lembro a senha. Clico na opção de \"Esqueci minha senha\", o sistema diz que enviou o link, mas não chega nada no meu e-mail (já olhei até na caixa de spam). Preciso de ajuda para acessar!");
        tAccess.setClient(client);
        tAccess.setAssignedSupport(support);
        tAccess.setCreatedAt(LocalDateTime.now().minusHours(5));

        Ticket tFinance = new Ticket();
        tFinance.setCategory(TicketCategory.FINANCE);
        tFinance.setPriority(TicketPriority.HIGH);
        tFinance.setStatus(TicketStatus.RESOLVED);
        tFinance.setTitle("Fatura cobrada com o valor errado este mês");
        tFinance.setDescription("Boa tarde! O valor normal da minha mensalidade é de R$ 50,00, mas acabei de olhar a fatura do cartão de crédito e veio cobrando R$ 150,00. Gostaria de entender o que aconteceu e solicitar o estorno da diferença.");
        tFinance.setClient(client);
        tFinance.setAssignedSupport(support);
        tFinance.setCreatedAt(LocalDateTime.now().minusDays(1));
        tFinance.setClosedAt(LocalDateTime.now().minusHours(2));

        ticketRepository.saveAll(List.of(tQuestion, tBug, tAccess, tFinance));


        Message m1 = new Message();
        m1.setContent("Verificando possível oscilação no sistema.");
        m1.setInternalNote(true);
        m1.setSender(support);
        m1.setTicket(tBug);
        m1.setCreatedAt(LocalDateTime.now().minusHours(2).minusMinutes(30));

        Message m2 = new Message();
        m2.setContent("Olá cliente! Peço que aguarde algumas horas, estamos passando por uma oscilação no sistema.");
        m2.setInternalNote(false);
        m2.setSender(support);
        m2.setTicket(tBug);
        m2.setCreatedAt(LocalDateTime.now().minusHours(2));

        Message m3 = new Message();
        m3.setContent("Olá! Enviamos um link de recuperação alternativo para você por SMS agora mesmo. Consegue testar e nos avisar se deu certo?");
        m3.setInternalNote(false);
        m3.setSender(support);
        m3.setTicket(tAccess);
        m3.setCreatedAt(LocalDateTime.now().minusHours(4));

        Message m4 = new Message();
        m4.setContent("cliente foi cobrado pelo plano anual por engano do sistema");
        m4.setInternalNote(true);
        m4.setSender(support);
        m4.setTicket(tFinance);
        m4.setCreatedAt(LocalDateTime.now().minusHours(20));

        Message m5 = new Message();
        m5.setContent("Olá! A cobrança foi indevida e a diferença já foi estornada, pedidos perdão pelo ocorrido.");
        m5.setInternalNote(false);
        m5.setSender(support);
        m5.setTicket(tFinance);
        m5.setCreatedAt(LocalDateTime.now().minusHours(2));

        messageRepository.saveAll(List.of(m1, m2, m3, m4, m5));

        System.out.println("✅ [DEMO SEEDER] Showcase de Portfólio preparado com sucesso!");
    }
}